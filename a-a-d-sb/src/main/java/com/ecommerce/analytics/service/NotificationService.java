package com.ecommerce.analytics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUser(String userId, String type, Object payload) {
        try {
            messagingTemplate.convertAndSendToUser(userId, "/queue/notifications",
                    Map.of("type", type, "data", payload));
        } catch (Exception e) {
            log.warn("Failed to send notification to user {}: {}", userId, e.getMessage());
        }
    }

    public void broadcastOrderUpdate(String orderId, String status) {
        messagingTemplate.convertAndSend("/topic/orders/" + orderId,
                Map.of("type", "ORDER_UPDATE", "orderId", orderId, "status", status));
    }
}
