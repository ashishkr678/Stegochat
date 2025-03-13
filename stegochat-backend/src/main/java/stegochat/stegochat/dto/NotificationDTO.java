package stegochat.stegochat.dto;

import lombok.*;
import stegochat.stegochat.entity.enums.NotificationType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private String id;
    private String username;
    private NotificationType type;
    private String referenceId;
    private String message;
    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
