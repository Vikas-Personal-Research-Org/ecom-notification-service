package com.ecom.notification.service;

import com.ecom.notification.dto.NotificationRequest;
import com.ecom.notification.dto.NotificationResponse;
import com.ecom.notification.dto.OrderEvent;
import com.ecom.notification.model.Notification;
import com.ecom.notification.model.NotificationChannel;
import com.ecom.notification.model.NotificationStatus;
import com.ecom.notification.model.NotificationType;
import com.ecom.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification(1L, 1L, NotificationType.ORDER_CONFIRMATION,
                NotificationChannel.EMAIL, "Order Confirmation", "Your order has been confirmed",
                NotificationStatus.SENT, LocalDateTime.now());
        notification.setId(1L);
    }

    @Test
    void sendNotification_ShouldCreateAndSendNotification() {
        NotificationRequest request = new NotificationRequest(1L, 1L,
                NotificationType.ORDER_CONFIRMATION, NotificationChannel.EMAIL,
                "Order Confirmation", "Your order has been confirmed");

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        NotificationResponse response = notificationService.sendNotification(request);

        assertNotNull(response);
        assertEquals(1L, response.userId());
        assertEquals(1L, response.orderId());
        assertEquals(NotificationType.ORDER_CONFIRMATION, response.type());
        assertEquals(NotificationChannel.EMAIL, response.channel());
        assertEquals(NotificationStatus.SENT, response.status());
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void handleOrderEvent_ShouldCreateOrderConfirmation() {
        OrderEvent event = new OrderEvent(1L, 1L, "ORDER_CONFIRMED", new BigDecimal("59.98"));

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        NotificationResponse response = notificationService.handleOrderEvent(event);

        assertNotNull(response);
        assertEquals(NotificationType.ORDER_CONFIRMATION, response.type());
        assertTrue(response.subject().contains("Order Confirmation"));
    }

    @Test
    void handleOrderEvent_ShouldCreatePaymentSuccess() {
        OrderEvent event = new OrderEvent(1L, 1L, "PAID", new BigDecimal("59.98"));

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        NotificationResponse response = notificationService.handleOrderEvent(event);

        assertNotNull(response);
        assertEquals(NotificationType.PAYMENT_SUCCESS, response.type());
        assertTrue(response.subject().contains("Payment Successful"));
    }

    @Test
    void handleOrderEvent_ShouldCreateShippedNotification() {
        OrderEvent event = new OrderEvent(1L, 1L, "SHIPPED", new BigDecimal("59.98"));

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        NotificationResponse response = notificationService.handleOrderEvent(event);

        assertNotNull(response);
        assertEquals(NotificationType.ORDER_SHIPPED, response.type());
        assertTrue(response.subject().contains("Order Shipped"));
    }

    @Test
    void handleOrderEvent_ShouldCreateCancelledNotification() {
        OrderEvent event = new OrderEvent(1L, 1L, "CANCELLED", new BigDecimal("59.98"));

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        NotificationResponse response = notificationService.handleOrderEvent(event);

        assertNotNull(response);
        assertEquals(NotificationType.ORDER_CANCELLED, response.type());
        assertTrue(response.subject().contains("Order Cancelled"));
    }

    @Test
    void handleOrderEvent_ShouldHandleDefaultStatus() {
        OrderEvent event = new OrderEvent(1L, 1L, "UNKNOWN_STATUS", new BigDecimal("59.98"));

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        NotificationResponse response = notificationService.handleOrderEvent(event);

        assertNotNull(response);
        assertTrue(response.subject().contains("Order Update"));
    }

    @Test
    void getNotificationsByUserId_ShouldReturnNotifications() {
        when(notificationRepository.findByUserId(1L)).thenReturn(List.of(notification));

        List<NotificationResponse> responses = notificationService.getNotificationsByUserId(1L);

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).userId());
    }

    @Test
    void getNotificationsByUserId_ShouldReturnEmptyList() {
        when(notificationRepository.findByUserId(99L)).thenReturn(List.of());

        List<NotificationResponse> responses = notificationService.getNotificationsByUserId(99L);

        assertTrue(responses.isEmpty());
    }

    @Test
    void getNotificationsByOrderId_ShouldReturnNotifications() {
        when(notificationRepository.findByOrderId(1L)).thenReturn(List.of(notification));

        List<NotificationResponse> responses = notificationService.getNotificationsByOrderId(1L);

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).orderId());
    }

    @Test
    void getNotificationsByOrderId_ShouldReturnEmptyList() {
        when(notificationRepository.findByOrderId(99L)).thenReturn(List.of());

        List<NotificationResponse> responses = notificationService.getNotificationsByOrderId(99L);

        assertTrue(responses.isEmpty());
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotifications() {
        Notification notification2 = new Notification(2L, 2L, NotificationType.ORDER_SHIPPED,
                NotificationChannel.SMS, "Order Shipped", "Your order has been shipped",
                NotificationStatus.SENT, LocalDateTime.now());
        notification2.setId(2L);

        when(notificationRepository.findAll()).thenReturn(List.of(notification, notification2));

        List<NotificationResponse> responses = notificationService.getAllNotifications();

        assertEquals(2, responses.size());
    }

    @Test
    void getAllNotifications_ShouldReturnEmptyList() {
        when(notificationRepository.findAll()).thenReturn(List.of());

        List<NotificationResponse> responses = notificationService.getAllNotifications();

        assertTrue(responses.isEmpty());
    }
}
