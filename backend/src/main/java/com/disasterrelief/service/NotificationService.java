package com.disasterrelief.service;

import com.disasterrelief.dto.response.NotificationResponse;
import com.disasterrelief.entity.*;
import com.disasterrelief.repository.NotificationRepository;
import com.disasterrelief.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void broadcastDisasterAlert(Disaster disaster) {
        List<User> allUsers = userRepository.findAll();
        String title = "New Disaster Alert: " + disaster.getTitle();
        String message = String.format("A %s %s has been reported at %s. Severity: %s",
                disaster.getSeverity(), disaster.getDisasterType().getName(),
                disaster.getLocationName(), disaster.getSeverity());

        allUsers.forEach(user -> {
            Notification notification = Notification.builder()
                    .user(user)
                    .title(title)
                    .message(message)
                    .notificationType(Notification.NotificationType.DISASTER_ALERT)
                    .referenceType("DISASTER")
                    .referenceId(disaster.getId())
                    .build();
            notificationRepository.save(notification);

            messagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/notifications",
                    toResponse(notification));
        });

        messagingTemplate.convertAndSend("/topic/disasters", disaster.getId());
        log.info("Broadcast disaster alert for disaster {}", disaster.getId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyVolunteerAssignment(Assignment assignment) {
        User volunteerUser = assignment.getVolunteer().getUser();
        String title = "New Assignment";
        String message = String.format("You have been assigned to disaster: %s. Please check your dashboard.",
                assignment.getDisaster().getTitle());

        Notification notification = Notification.builder()
                .user(volunteerUser)
                .title(title)
                .message(message)
                .notificationType(Notification.NotificationType.ASSIGNMENT)
                .referenceType("ASSIGNMENT")
                .referenceId(assignment.getId())
                .build();
        notificationRepository.save(notification);

        messagingTemplate.convertAndSendToUser(volunteerUser.getUsername(), "/queue/notifications",
                toResponse(notification));
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendLowStockAlert(Inventory inventory) {
        userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getName() == Role.RoleName.ROLE_ADMIN || r.getName() == Role.RoleName.ROLE_COORDINATOR))
                .forEach(admin -> {
                    Notification notification = Notification.builder()
                            .user(admin)
                            .title("Low Stock Alert")
                            .message(String.format("Item '%s' is running low (Qty: %d, Threshold: %d)",
                                    inventory.getItemName(), inventory.getQuantity(), inventory.getMinThreshold()))
                            .notificationType(Notification.NotificationType.INVENTORY_ALERT)
                            .referenceType("INVENTORY")
                            .referenceId(inventory.getId())
                            .build();
                    notificationRepository.save(notification);
                    messagingTemplate.convertAndSendToUser(admin.getUsername(), "/queue/notifications",
                            toResponse(notification));
                });
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(String username, Boolean unreadOnly, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow();
        Page<Notification> notifications = Boolean.TRUE.equals(unreadOnly)
                ? notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(user.getId(), false, pageable)
                : notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return notifications.map(this::toResponse);
    }

    @Transactional
    public int markAllAsRead(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return notificationRepository.markAllAsRead(user.getId());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return notificationRepository.countByUserIdAndIsRead(user.getId(), false);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .notificationType(n.getNotificationType())
                .referenceType(n.getReferenceType())
                .referenceId(n.getReferenceId())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
