package com.ecom.notification.service;

import com.ecom.notification.dto.NotificationRequest;
import com.ecom.notification.dto.NotificationResponse;
import com.ecom.notification.dto.OrderEvent;
import com.ecom.notification.model.Notification;
import com.ecom.notification.model.NotificationChannel;
import com.ecom.notification.model.NotificationStatus;
import com.ecom.notification.model.NotificationType;
import com.ecom.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationResponse sendNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.userId());
        notification.setOrderId(request.orderId());
        notification.setType(request.type());
        notification.setChannel(request.channel());
        notification.setSubject(request.subject());
        notification.setMessage(request.message());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setCreatedAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);

        logger.info("SENDING {} to user {}: {} - {}",
                notification.getChannel(), notification.getUserId(),
                notification.getSubject(), notification.getMessage());

        notification.setStatus(NotificationStatus.SENT);
        notification = notificationRepository.save(notification);

        return mapToResponse(notification);
    }

    public NotificationResponse handleOrderEvent(OrderEvent event) {
        String subject;
        String message;
        NotificationType type;

        switch (event.orderStatus()) {
            case "ORDER_CONFIRMED" -> {
                subject = "Order Confirmation - Order #" + event.orderId();
                message = "Your order #" + event.orderId() + " has been confirmed. Total amount: $" + event.totalAmount();
                type = NotificationType.ORDER_CONFIRMATION;
            }
            case "PAID" -> {
                subject = "Payment Successful - Order #" + event.orderId();
                message = "Payment of $" + event.totalAmount() + " for order #" + event.orderId() + " was successful.";
                type = NotificationType.PAYMENT_SUCCESS;
            }
            case "SHIPPED" -> {
                subject = "Order Shipped - Order #" + event.orderId();
                message = "Your order #" + event.orderId() + " has been shipped.";
                type = NotificationType.ORDER_SHIPPED;
            }
            case "CANCELLED" -> {
                subject = "Order Cancelled - Order #" + event.orderId();
                message = "Your order #" + event.orderId() + " has been cancelled.";
                type = NotificationType.ORDER_CANCELLED;
            }
            default -> {
                subject = "Order Update - Order #" + event.orderId();
                message = "Your order #" + event.orderId() + " status has been updated to " + event.orderStatus() + ".";
                type = NotificationType.ORDER_CONFIRMATION;
            }
        }

        NotificationRequest request = new NotificationRequest(
                event.userId(),
                event.orderId(),
                type,
                NotificationChannel.EMAIL,
                subject,
                message
        );

        return sendNotification(request);
    }

    public List<NotificationResponse> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> getNotificationsByOrderId(Long orderId) {
        return notificationRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getOrderId(),
                notification.getType(),
                notification.getChannel(),
                notification.getSubject(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getCreatedAt()
        );
    }
}
