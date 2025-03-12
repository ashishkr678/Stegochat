package stegochat.stegochat.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import stegochat.stegochat.dto.NotificationDTO;
import stegochat.stegochat.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ✅ Get all notifications for the logged-in user
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(HttpServletRequest request) {
        return ResponseEntity.ok(notificationService.getUserNotifications(request));
    }

    // ✅ Mark a single notification as read
    @PostMapping("/mark-read/{id}")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read."));
    }

    // Bulk Mark Notifications as Read (Efficient DB Update)
    @MessageMapping("/read-notifications")
    public void markNotificationsAsRead(@RequestBody List<String> notificationIds) {
        notificationService.markNotificationsAsRead(notificationIds);
    }

    // Mark All Notifications as Read (Clicking Notification Tab)
    @PostMapping("/mark-all-read")
    public ResponseEntity<Map<String, String>> markAllNotificationsAsRead(HttpServletRequest request) {
        notificationService.markAllNotificationsAsRead(request);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read."));
    }

    // Delete a single notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(Map.of("message", "Notification deleted successfully."));
    }

    // Delete all notifications for a user
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearNotifications(HttpServletRequest request) {
        notificationService.deleteAllNotifications(request);
        return ResponseEntity.ok(Map.of("message", "All notifications cleared."));
    }
}
