package stegochat.stegochat.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import stegochat.stegochat.dto.NotificationDTO;
import stegochat.stegochat.entity.NotificationsEntity;
import stegochat.stegochat.entity.enums.NotificationType;
import stegochat.stegochat.mapper.NotificationMapper;
import stegochat.stegochat.repository.NotificationRepository;
import stegochat.stegochat.security.CookieUtil;
import stegochat.stegochat.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MongoTemplate mongoTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void sendNotification(String username, String message, NotificationType type, String referenceId) {
        NotificationsEntity notification = NotificationsEntity.create(username, type, message, referenceId);
        NotificationsEntity savedNotification = notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/user/" + username + "/queue/notifications",
                NotificationMapper.toDTO(savedNotification));
    }

    // Get All Notifications (Automatically fetches username)
    @Override
    public List<NotificationDTO> getUserNotifications(HttpServletRequest request) {

        String username = CookieUtil.extractUsernameFromCookie(request);

        return notificationRepository.findByUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(NotificationMapper::toDTO)
                .toList();
    }

    // Mark a specific Notification as Read
    @Override
    @Transactional
    public void markAsRead(String notificationId) {
        NotificationsEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    // Bulk Mark Notifications as Read
    @Override
    @Transactional
    public void markNotificationsAsRead(List<String> notificationIds) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, NotificationsEntity.class);
        bulkOps.updateMulti(
                new org.springframework.data.mongodb.core.query.Query(
                        org.springframework.data.mongodb.core.query.Criteria.where("id").in(notificationIds)),
                new org.springframework.data.mongodb.core.query.Update().set("isRead", true).set("readAt",
                        LocalDateTime.now()));
        bulkOps.execute();
    }

    // Mark All Notifications as Read (Clicking Notification Tab)
    @Override
    @Transactional
    public void markAllNotificationsAsRead(HttpServletRequest request) {

        String username = CookieUtil.extractUsernameFromCookie(request);

        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, NotificationsEntity.class);
        bulkOps.updateMulti(
                new org.springframework.data.mongodb.core.query.Query(
                        org.springframework.data.mongodb.core.query.Criteria.where("username").is(username)
                                .and("isRead").is(false)),
                new org.springframework.data.mongodb.core.query.Update().set("isRead", true).set("readAt",
                        LocalDateTime.now()));
        bulkOps.execute();
    }

    // Delete a single notification
    @Override
    @Transactional
    public void deleteNotification(String notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // Delete all notifications for a user
    @Override
    @Transactional
    public void deleteAllNotifications(HttpServletRequest request) {

        String username = CookieUtil.extractUsernameFromCookie(request);

        notificationRepository.deleteByUsername(username);
    }
}
