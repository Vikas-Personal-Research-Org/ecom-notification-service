package com.ecom.notification.dto;

import com.ecom.notification.model.NotificationChannel;
import com.ecom.notification.model.NotificationStatus;
import com.ecom.notification.model.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long userId,
        Long orderId,
        NotificationType type,
        NotificationChannel channel,
        String subject,
        String message,
        NotificationStatus status,
        LocalDateTime createdAt
) {
}
