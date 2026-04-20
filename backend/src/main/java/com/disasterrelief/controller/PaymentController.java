package com.disasterrelief.controller;

import com.disasterrelief.dto.request.PaymentOrderRequest;
import com.disasterrelief.dto.request.PaymentVerifyRequest;
import com.disasterrelief.dto.response.PaymentOrderResponse;
import com.disasterrelief.service.impl.RazorpayServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayServiceImpl razorpayService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(@Valid @RequestBody PaymentOrderRequest request) {
        return ResponseEntity.ok(razorpayService.createOrder(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(@Valid @RequestBody PaymentVerifyRequest request) {
        boolean success = razorpayService.verifyAndCapturePayment(request);
        return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "Payment verified and captured successfully" : "Payment verification failed"
        ));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        log.info("Received Razorpay webhook event");
        razorpayService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
}
