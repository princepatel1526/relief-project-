package com.disasterrelief.exception;

public class PaymentException extends RuntimeException {
    private final String paymentOrderId;

    public PaymentException(String message) {
        super(message);
        this.paymentOrderId = null;
    }

    public PaymentException(String message, String paymentOrderId) {
        super(message);
        this.paymentOrderId = paymentOrderId;
    }

    public String getPaymentOrderId() { return paymentOrderId; }
}
