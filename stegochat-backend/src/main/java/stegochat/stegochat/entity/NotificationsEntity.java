package stegochat.stegochat.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import stegochat.stegochat.entity.enums.NotificationType;

@Document(collection = "notifications")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationsEntity extends BaseEntity {

    @Id
    private String id;

    @Indexed
    private String username;

    private NotificationType type;
    private String message;

    @Builder.Default
    private boolean isRead = false;

    @Indexed
    private String referenceId;

    private LocalDateTime readAt;

    // Static factory method for cleaner creation
    public static NotificationsEntity create(String username, NotificationType type, String message, String referenceId) {
        NotificationsEntity notification = NotificationsEntity.builder()
                .username(username)
                .type(type)
                .message(message)
                .referenceId(referenceId)
                .isRead(false)
                .readAt(null)
                .build();
        
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }
}
