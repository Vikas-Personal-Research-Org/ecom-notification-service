package com.ecom.notification.controller;

import com.ecom.notification.dto.NotificationRequest;
import com.ecom.notification.dto.NotificationResponse;
import com.ecom.notification.dto.OrderEvent;
import com.ecom.notification.model.NotificationChannel;
import com.ecom.notification.model.NotificationStatus;
import com.ecom.notification.model.NotificationType;
import com.ecom.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationResponse notificationResponse;

    @BeforeEach
    void setUp() {
        notificationResponse = new NotificationResponse(1L, 1L, 1L,
                NotificationType.ORDER_CONFIRMATION, NotificationChannel.EMAIL,
                "Order Confirmation", "Your order has been confirmed",
                NotificationStatus.SENT, LocalDateTime.now());
    }

    @Test
    void sendNotification_ShouldReturn201() throws Exception {
        when(notificationService.sendNotification(any(NotificationRequest.class)))
                .thenReturn(notificationResponse);

        NotificationRequest request = new NotificationRequest(1L, 1L,
                NotificationType.ORDER_CONFIRMATION, NotificationChannel.EMAIL,
                "Order Confirmation", "Your order has been confirmed");

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("ORDER_CONFIRMATION"))
                .andExpect(jsonPath("$.channel").value("EMAIL"))
                .andExpect(jsonPath("$.status").value("SENT"));
    }

    @Test
    void handleOrderEvent_ShouldReturn201() throws Exception {
        when(notificationService.handleOrderEvent(any(OrderEvent.class)))
                .thenReturn(notificationResponse);

        OrderEvent event = new OrderEvent(1L, 1L, "ORDER_CONFIRMED", new BigDecimal("59.98"));

        mockMvc.perform(post("/api/notifications/order-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("ORDER_CONFIRMATION"));
    }

    @Test
    void getNotificationsByUserId_ShouldReturnNotifications() throws Exception {
        when(notificationService.getNotificationsByUserId(1L))
                .thenReturn(List.of(notificationResponse));

        mockMvc.perform(get("/api/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void getNotificationsByUserId_ShouldReturnEmptyList() throws Exception {
        when(notificationService.getNotificationsByUserId(99L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/user/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getNotificationsByOrderId_ShouldReturnNotifications() throws Exception {
        when(notificationService.getNotificationsByOrderId(1L))
                .thenReturn(List.of(notificationResponse));

        mockMvc.perform(get("/api/notifications/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1));
    }

    @Test
    void getNotificationsByOrderId_ShouldReturnEmptyList() throws Exception {
        when(notificationService.getNotificationsByOrderId(99L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/notifications/order/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllNotifications_ShouldReturnNotifications() throws Exception {
        when(notificationService.getAllNotifications())
                .thenReturn(List.of(notificationResponse));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllNotifications_ShouldReturnEmptyList() throws Exception {
        when(notificationService.getAllNotifications())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
