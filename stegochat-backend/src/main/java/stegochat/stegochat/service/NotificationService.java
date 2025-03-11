package stegochat.stegochat.service;

import stegochat.stegochat.dto.NotificationDTO;
import stegochat.stegochat.entity.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    void createNotification(String username, String message, NotificationType type, String referenceId);

    List<NotificationDTO> getUserNotifications(String username);

    void markAsRead(String notificationId);

    void deleteNotification(String notificationId);

    void deleteAllNotifications(String username);

}
