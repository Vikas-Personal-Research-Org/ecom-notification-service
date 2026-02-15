package com.ecom.notification.dto;

import com.ecom.notification.model.NotificationChannel;
import com.ecom.notification.model.NotificationType;

public record NotificationRequest(
        Long userId,
        Long orderId,
        NotificationType type,
        NotificationChannel channel,
        String subject,
        String message
) {
}
