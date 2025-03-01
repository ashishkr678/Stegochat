package stegochat.stegochat.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notifications {
    @Id
    private ObjectId id;  // MongoDB Auto-generated ID

    private String username;  // Recipient username (Linked to Users)
    private NotificationType type;  // Type of notification
    private String message;  // Notification text (e.g., "You received a new message")

    private boolean isRead;  // Has the user seen this notification?
    private LocalDateTime createdAt;  // When the notification was created
    private LocalDateTime readAt;  // When the notification was read (optional)

    private Map<String, String> metadata;  // Extra details (e.g., chatId, senderUsername)
    private int unreadMessageCount;  // Tracks new messages for each chat (WhatsApp style)

    public enum NotificationType {
        MESSAGE, // New message received
        FRIEND_REQUEST, // New friend request
        FRIEND_REQUEST_ACCEPTED, // Friend request accepted
        SYSTEM_UPDATE, // App updates or alerts
        SECURITY_ALERT // Security-related notifications
    }
}
