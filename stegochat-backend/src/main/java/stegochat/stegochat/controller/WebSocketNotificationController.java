package stegochat.stegochat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.entity.NotificationsEntity;
import stegochat.stegochat.entity.enums.NotificationType;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.repository.NotificationRepository;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WebSocketNotificationController {

    private final NotificationRepository notificationRepository;
    private final MongoTemplate mongoTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    // ✅ Send Notification to a specific user with type
    public void sendNotification(String username, String message, String referenceId, NotificationType type) {
        NotificationsEntity notification = NotificationsEntity.builder()
                .username(username)
                .message(message)
                .referenceId(referenceId)
                .type(type)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/user/" + username + "/queue/notifications", notification);
    }

    // ✅ Mark Notifications as Read in Real-Time
    @MessageMapping("/notifications/read")
    public void markNotificationsAsRead(List<String> notificationIds, SimpMessageHeaderAccessor headerAccessor) {
        HttpSession session = (HttpSession) headerAccessor.getSessionAttributes().get("session");
        UserDTO user = (session != null) ? (UserDTO) session.getAttribute("userProfile") : null;

        if (user == null) {
            throw new BadRequestException("User session expired. Please reconnect.");
        }

        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, NotificationsEntity.class);
        Query query = new Query(Criteria.where("id").in(notificationIds));
        Update update = new Update().set("isRead", true).set("readAt", LocalDateTime.now());

        bulkOps.updateMulti(query, update);
        bulkOps.execute();

        List<NotificationsEntity> updatedNotifications = notificationRepository
                .findByUsernameOrderByCreatedAtDesc(user.getUsername());
        messagingTemplate.convertAndSend("/user/" + user.getUsername() + "/queue/notifications", updatedNotifications);
    }
}
