package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findBySessionSessionIdOrderByCreatedAtAsc(String sessionId);
}
