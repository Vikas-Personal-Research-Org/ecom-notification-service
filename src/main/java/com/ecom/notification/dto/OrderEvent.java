package com.ecom.notification.dto;

import java.math.BigDecimal;

public record OrderEvent(
        Long orderId,
        Long userId,
        String orderStatus,
        BigDecimal totalAmount
) {
}
