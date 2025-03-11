package stegochat.stegochat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stegochat.stegochat.dto.NotificationDTO;
import stegochat.stegochat.service.NotificationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ✅ Get all notifications for logged-in user
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(@RequestParam String username) {
        return ResponseEntity.ok(notificationService.getUserNotifications(username));
    }

    // ✅ Mark a notification as read
    @PostMapping("/mark-as-read/{id}")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read."));
    }

    // ✅ Delete a single notification
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(Map.of("message", "Notification deleted successfully."));
    }

    // ✅ Delete all notifications for a user
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearNotifications(@RequestParam String username) {
        notificationService.deleteAllNotifications(username);
        return ResponseEntity.ok(Map.of("message", "All notifications cleared."));
    }
}
