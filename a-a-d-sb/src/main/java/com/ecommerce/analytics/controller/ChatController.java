package com.ecommerce.analytics.controller;

import com.ecommerce.analytics.dto.request.ChatAskRequest;
import com.ecommerce.analytics.dto.request.CreateChatSessionRequest;
import com.ecommerce.analytics.dto.response.*;
import com.ecommerce.analytics.service.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "AI Chatbot (Text2SQL)")
public class ChatController {

    private final ChatbotService chatbotService;

    @PostMapping("/sessions")
    @Operation(summary = "Create a new chat session")
    public ResponseEntity<ChatSessionResponse> createSession(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody CreateChatSessionRequest request) {
        return ResponseEntity.ok(chatbotService.createSession(principal.getUsername(), request));
    }

    @GetMapping("/sessions")
    @Operation(summary = "List all chat sessions for the current user")
    public ResponseEntity<PagedResponse<ChatSessionResponse>> getSessions(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        var result = chatbotService.getSessions(principal.getUsername(), pageable);
        return ResponseEntity.ok(PagedResponse.<ChatSessionResponse>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build());
    }

    @GetMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "Get all messages in a session")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String sessionId) {
        return ResponseEntity.ok(chatbotService.getMessages(principal.getUsername(), sessionId));
    }

    @PostMapping("/ask")
    @Operation(summary = "Ask a natural-language question (Text2SQL)")
    public ResponseEntity<ChatAskResponse> ask(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ChatAskRequest request) {
        return ResponseEntity.ok(chatbotService.ask(principal.getUsername(), request));
    }

    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "Delete a chat session and all its messages")
    public ResponseEntity<Void> deleteSession(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable String sessionId) {
        chatbotService.deleteSession(principal.getUsername(), sessionId);
        return ResponseEntity.noContent().build();
    }
}
