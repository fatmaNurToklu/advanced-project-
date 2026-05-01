package com.ecommerce.analytics.repository;

import com.ecommerce.analytics.model.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, String> {
    Page<ChatSession> findByUserUserIdOrderByUpdatedAtDesc(String userId, Pageable pageable);
    Optional<ChatSession> findBySessionIdAndUserUserId(String sessionId, String userId);
}
