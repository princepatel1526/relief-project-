package com.disasterrelief.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentOrderRequest {
    @NotNull @DecimalMin("1.00")
    private BigDecimal amount;

    private String currency = "INR";

    @NotBlank @Size(max = 100)
    private String donorName;

    @Email @Size(max = 100)
    private String donorEmail;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
    private String donorPhone;

    private Long disasterId;

    private String donationType = "MONETARY";

    private Boolean isAnonymous = false;

    private String description;
}
