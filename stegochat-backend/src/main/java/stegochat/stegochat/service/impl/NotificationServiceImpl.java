package stegochat.stegochat.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stegochat.stegochat.dto.NotificationDTO;
import stegochat.stegochat.entity.NotificationsEntity;
import stegochat.stegochat.entity.enums.NotificationType;
import stegochat.stegochat.mapper.NotificationMapper;
import stegochat.stegochat.repository.NotificationRepository;
import stegochat.stegochat.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate; // ✅ WebSocket Messaging

    // ✅ Create a notification and send a real-time update
    @Override
    @Transactional
    public void createNotification(String username, String message, NotificationType type, String referenceId) {
        NotificationsEntity notification = NotificationsEntity.builder()
                .username(username)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .isRead(false)
                .build();

        NotificationsEntity savedNotification = notificationRepository.save(notification);

        // ✅ Send real-time notification via WebSocket
        messagingTemplate.convertAndSend("/topic/notifications/" + username, NotificationMapper.toDTO(savedNotification));
    }

    // ✅ Fetch user notifications
    @Override
    public List<NotificationDTO> getUserNotifications(String username) {
        return notificationRepository.findByUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ Mark notification as read
    @Override
    @Transactional
    public void markAsRead(String notificationId) {
        NotificationsEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    // ✅ Delete a single notification
    @Override
    @Transactional
    public void deleteNotification(String notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // ✅ Delete all notifications for a user
    @Override
    @Transactional
    public void deleteAllNotifications(String username) {
        notificationRepository.deleteByUsername(username);
    }
}
