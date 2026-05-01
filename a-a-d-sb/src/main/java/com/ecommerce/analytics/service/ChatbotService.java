package com.ecommerce.analytics.service;

import com.ecommerce.analytics.dto.request.ChatAskRequest;
import com.ecommerce.analytics.dto.request.CreateChatSessionRequest;
import com.ecommerce.analytics.dto.response.ChatAskResponse;
import com.ecommerce.analytics.dto.response.ChatMessageResponse;
import com.ecommerce.analytics.dto.response.ChatSessionResponse;
import com.ecommerce.analytics.exception.ResourceNotFoundException;
import com.ecommerce.analytics.model.ChatMessage;
import com.ecommerce.analytics.model.ChatSession;
import com.ecommerce.analytics.model.User;
import com.ecommerce.analytics.repository.ChatMessageRepository;
import com.ecommerce.analytics.repository.ChatSessionRepository;
import com.ecommerce.analytics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    @Value("${app.chatbot.service-url}")
    private String chatbotServiceUrl;

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public ChatSessionResponse createSession(String userId, CreateChatSessionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ChatSession session = ChatSession.builder()
                .user(user)
                .title(request.getTitle() != null ? request.getTitle() : "New Chat")
                .build();
        session = chatSessionRepository.save(session);
        return toChatSessionResponse(session);
    }

    @Transactional(readOnly = true)
    public Page<ChatSessionResponse> getSessions(String userId, Pageable pageable) {
        return chatSessionRepository.findByUserUserIdOrderByUpdatedAtDesc(userId, pageable)
                .map(this::toChatSessionResponse);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(String userId, String sessionId) {
        chatSessionRepository.findBySessionIdAndUserUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));
        return chatMessageRepository.findBySessionSessionIdOrderByCreatedAtAsc(sessionId)
                .stream().map(m -> ChatMessageResponse.builder()
                        .messageId(m.getMessageId())
                        .role(m.getRole())
                        .content(m.getContent())
                        .createdAt(m.getCreatedAt())
                        .build()).collect(Collectors.toList());
    }

    @Transactional
    public ChatAskResponse ask(String userId, ChatAskRequest request) {
        ChatSession session = chatSessionRepository.findBySessionIdAndUserUserId(request.getSessionId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        ChatMessage userMessage = ChatMessage.builder()
                .session(session)
                .role("user")
                .content(request.getQuestion())
                .build();
        chatMessageRepository.save(userMessage);

        String answer;
        String sqlQuery = null;
        String visualizationCode = null;

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("question", request.getQuestion());
            payload.put("user_id", userId);
            payload.put("session_id", request.getSessionId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    chatbotServiceUrl + "/ask", HttpMethod.POST, entity,
                    new ParameterizedTypeReference<>() {});

            Map<String, Object> body = response.getBody();
            if (body != null) {
                answer = (String) body.getOrDefault("answer", "No answer returned.");
                sqlQuery = (String) body.get("sql_query");
                visualizationCode = (String) body.get("visualization_code");
            } else {
                answer = "No answer returned.";
            }
        } catch (Exception e) {
            log.warn("Chatbot service unavailable: {}", e.getMessage());
            answer = "AI service is temporarily unavailable. Please try again later.";
        }

        ChatMessage assistantMessage = ChatMessage.builder()
                .session(session)
                .role("assistant")
                .content(answer)
                .build();
        chatMessageRepository.save(assistantMessage);

        return ChatAskResponse.builder()
                .sessionId(request.getSessionId())
                .question(request.getQuestion())
                .answer(answer)
                .sqlQuery(sqlQuery)
                .visualizationCode(visualizationCode)
                .hasVisualization(visualizationCode != null)
                .build();
    }

    @Transactional
    public void deleteSession(String userId, String sessionId) {
        ChatSession session = chatSessionRepository.findBySessionIdAndUserUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));
        chatSessionRepository.delete(session);
    }

    private ChatSessionResponse toChatSessionResponse(ChatSession s) {
        return ChatSessionResponse.builder()
                .sessionId(s.getSessionId())
                .title(s.getTitle())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .messageCount(s.getMessages().size())
                .build();
    }
}
