package com.disasterrelief.service;

import com.disasterrelief.entity.AuditLog;
import com.disasterrelief.entity.User;
import com.disasterrelief.repository.AuditLogRepository;
import com.disasterrelief.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    /**
     * Write an audit log entry asynchronously so it never blocks the main request.
     *
     * @param action     Verb describing the action: "LOGIN", "STATUS_CHANGE", "ASSIGNMENT", etc.
     * @param entityType Entity class name: "Disaster", "ReliefRequest", "Assignment", etc.
     * @param entityId   Primary key of the affected entity (may be null).
     * @param oldValue   JSON-serialised old value (may be null).
     * @param newValue   JSON-serialised new value (may be null).
     */
    @Async
    public void log(String action, String entityType, Long entityId,
                    String oldValue, String newValue) {
        try {
            String username = currentUsername();
            User user = null;
            if (username != null && !username.equals("anonymousUser")) {
                user = userRepository.findByUsername(username)
                        .or(() -> userRepository.findByEmail(username))
                        .orElse(null);
            }

            auditLogRepository.save(AuditLog.builder()
                    .user(user)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .build());
        } catch (Exception ex) {
            log.warn("Failed to write audit log: {}", ex.getMessage());
        }
    }

    /** Convenience — no old/new value */
    @Async
    public void log(String action, String entityType, Long entityId) {
        log(action, entityType, entityId, null, null);
    }

    private String currentUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null ? auth.getName() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
