package stegochat.stegochat.service;

import stegochat.stegochat.dto.NotificationDTO;
import stegochat.stegochat.entity.enums.NotificationType;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

public interface NotificationService {

    void sendNotification(String username, String message, NotificationType type, String referenceId);
    
    List<NotificationDTO> getUserNotifications(HttpServletRequest request);

    void markAsRead(String notificationId);

    void markNotificationsAsRead(List<String> notificationIds);

    void markAllNotificationsAsRead(HttpServletRequest request);

    void deleteNotification(String notificationId);

    void deleteAllNotifications(HttpServletRequest request);
    
}
