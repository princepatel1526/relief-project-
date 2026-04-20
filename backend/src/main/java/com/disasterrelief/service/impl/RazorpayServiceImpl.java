package com.disasterrelief.service.impl;

import com.disasterrelief.dto.request.PaymentOrderRequest;
import com.disasterrelief.dto.request.PaymentVerifyRequest;
import com.disasterrelief.dto.response.PaymentOrderResponse;
import com.disasterrelief.entity.*;
import com.disasterrelief.exception.PaymentException;
import com.disasterrelief.exception.ResourceNotFoundException;
import com.disasterrelief.repository.*;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final DonationRepository donationRepository;
    private final DisasterRepository disasterRepository;
    private final UserRepository userRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @Transactional
    public PaymentOrderResponse createOrder(PaymentOrderRequest request) {
        long amountInPaise = request.getAmount().multiply(BigDecimal.valueOf(100)).longValue();

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", request.getCurrency());
        orderRequest.put("receipt", "rcpt_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", 1);

        Order razorpayOrder;
        try {
            razorpayOrder = razorpayClient.orders.create(orderRequest);
        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order: {}", e.getMessage());
            throw new PaymentException("Failed to create payment order: " + e.getMessage());
        }

        String authName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(authName).orElse(null);

        Payment payment = Payment.builder()
                .user(user)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(Payment.PaymentStatus.CREATED)
                .provider(Payment.PaymentProvider.RAZORPAY)
                .paymentOrderId(razorpayOrder.get("id"))
                .donorName(request.getDonorName())
                .donorEmail(request.getDonorEmail())
                .donorPhone(request.getDonorPhone())
                .build();

        paymentRepository.save(payment);

        Disaster disaster = request.getDisasterId() != null
                ? disasterRepository.findById(request.getDisasterId()).orElse(null)
                : null;

        Donation donation = Donation.builder()
                .payment(payment)
                .disaster(disaster)
                .amount(request.getAmount())
                .donationType(Donation.DonationType.valueOf(request.getDonationType()))
                .description(request.getDescription())
                .status(Donation.DonationStatus.PENDING)
                .donorName(request.getDonorName())
                .donorEmail(request.getDonorEmail())
                .donorPhone(request.getDonorPhone())
                .isAnonymous(request.getIsAnonymous())
                .build();

        donationRepository.save(donation);

        log.info("Payment order created: orderId={}, amount={} {}", razorpayOrder.get("id"), request.getAmount(), request.getCurrency());

        return PaymentOrderResponse.builder()
                .paymentDbId(payment.getId())
                .orderId(razorpayOrder.get("id"))
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .keyId(razorpayKeyId)
                .donorName(request.getDonorName())
                .donorEmail(request.getDonorEmail())
                .donorPhone(request.getDonorPhone())
                .description(request.getDescription())
                .build();
    }

    @Transactional
    public boolean verifyAndCapturePayment(PaymentVerifyRequest request) {
        if (paymentRepository.existsByPaymentId(request.getRazorpayPaymentId())) {
            log.warn("Duplicate payment verification attempt for paymentId: {}", request.getRazorpayPaymentId());
            return true;
        }

        boolean signatureValid = verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        Payment payment = paymentRepository.findByPaymentOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new PaymentException("Payment record not found for order: " + request.getRazorpayOrderId()));

        if (!signatureValid) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason("Signature verification failed");
            paymentRepository.save(payment);
            log.warn("Payment signature verification FAILED for order: {}", request.getRazorpayOrderId());
            throw new PaymentException("Payment signature verification failed", request.getRazorpayOrderId());
        }

        payment.setPaymentId(request.getRazorpayPaymentId());
        payment.setSignature(request.getRazorpaySignature());
        payment.setStatus(Payment.PaymentStatus.CAPTURED);
        paymentRepository.save(payment);

        donationRepository.findAll().stream()
                .filter(d -> d.getPayment() != null && d.getPayment().getId().equals(payment.getId()))
                .forEach(donation -> {
                    donation.setStatus(Donation.DonationStatus.CONFIRMED);
                    donationRepository.save(donation);
                });

        log.info("Payment captured successfully: orderId={}, paymentId={}", request.getRazorpayOrderId(), request.getRazorpayPaymentId());
        return true;
    }

    @Transactional
    public void handleWebhook(String payload, String signature) {
        if (!verifyWebhookSignature(payload, signature)) {
            log.warn("Invalid webhook signature received");
            throw new PaymentException("Invalid webhook signature");
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.optString("event");
        JSONObject paymentEntity = event.optJSONObject("payload") != null
                ? event.getJSONObject("payload").optJSONObject("payment") != null
                    ? event.getJSONObject("payload").getJSONObject("payment").optJSONObject("entity")
                    : null
                : null;

        if (paymentEntity == null) return;

        String orderId = paymentEntity.optString("order_id");
        String paymentId = paymentEntity.optString("id");

        paymentRepository.findByPaymentOrderId(orderId).ifPresent(payment -> {
            boolean alreadyProcessed = paymentEventRepository
                    .existsByPaymentIdAndEventType(payment.getId(), eventType);
            if (alreadyProcessed) {
                log.info("Webhook event {} already processed for payment {}", eventType, payment.getId());
                return;
            }

            PaymentEvent paymentEvent = PaymentEvent.builder()
                    .payment(payment)
                    .eventType(eventType)
                    .payload(payload)
                    .processed(true)
                    .build();
            paymentEventRepository.save(paymentEvent);

            switch (eventType) {
                case "payment.captured" -> {
                    if (payment.getStatus() != Payment.PaymentStatus.CAPTURED) {
                        payment.setPaymentId(paymentId);
                        payment.setStatus(Payment.PaymentStatus.CAPTURED);
                        paymentRepository.save(payment);
                        updateDonationStatus(payment.getId(), Donation.DonationStatus.CONFIRMED);
                        log.info("Payment captured via webhook: orderId={}", orderId);
                    }
                }
                case "payment.failed" -> {
                    payment.setStatus(Payment.PaymentStatus.FAILED);
                    payment.setFailureReason(paymentEntity.optString("error_description", "Payment failed"));
                    paymentRepository.save(payment);
                    log.warn("Payment failed via webhook: orderId={}", orderId);
                }
                case "refund.created" -> {
                    payment.setStatus(Payment.PaymentStatus.REFUNDED);
                    paymentRepository.save(payment);
                    updateDonationStatus(payment.getId(), Donation.DonationStatus.CANCELLED);
                }
                default -> log.debug("Unhandled webhook event: {}", eventType);
            }
        });
    }

    private void updateDonationStatus(Long paymentId, Donation.DonationStatus status) {
        donationRepository.findAll().stream()
                .filter(d -> d.getPayment() != null && d.getPayment().getId().equals(paymentId))
                .forEach(donation -> {
                    donation.setStatus(status);
                    donationRepository.save(donation);
                });
    }

    private boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String data = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hashBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String computed = HexFormat.of().formatHex(hashBytes);
            return computed.equals(signature);
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage());
            return false;
        }
    }

    private boolean verifyWebhookSignature(String payload, String receivedSignature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hashBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computed = HexFormat.of().formatHex(hashBytes);
            return computed.equals(receivedSignature);
        } catch (Exception e) {
            log.error("Webhook signature verification error: {}", e.getMessage());
            return false;
        }
    }
}
