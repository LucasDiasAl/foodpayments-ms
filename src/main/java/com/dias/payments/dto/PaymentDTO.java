package com.dias.payments.dto;

import com.dias.payments.model.Status;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class PaymentDTO {
    private Long id;
    private BigDecimal amount;
    private String name;
    private String number;
    private String expire;
    private String code;
    private Status status;
    private Long orderId;
    private Long paymentMethodId;
}
