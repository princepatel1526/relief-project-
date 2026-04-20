package com.disasterrelief.controller;

import com.disasterrelief.dto.response.NotificationResponse;
import com.disasterrelief.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) Boolean unreadOnly,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getUserNotifications(user.getUsername(), unreadOnly, pageable));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user.getUsername())));
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<Map<String, Integer>> markAllRead(@AuthenticationPrincipal UserDetails user) {
        int updated = notificationService.markAllAsRead(user.getUsername());
        return ResponseEntity.ok(Map.of("updated", updated));
    }
}
