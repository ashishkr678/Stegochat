package stegochat.stegochat.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import stegochat.stegochat.entity.enums.NotificationType;

@Document(collection = "notifications")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notifications extends BaseEntity {
    @Id
    private String id;

    private String username;
    private NotificationType type;
    private String message;
    private boolean isRead;
    private LocalDateTime readAt;

    // ✅ Metadata for Notification Source Tracking
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
}
