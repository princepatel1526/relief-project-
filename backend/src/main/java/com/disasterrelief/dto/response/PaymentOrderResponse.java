package com.disasterrelief.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class PaymentOrderResponse {
    private Long paymentDbId;
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String keyId;
    private String donorName;
    private String donorEmail;
    private String donorPhone;
    private String description;
}
