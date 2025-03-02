package stegochat.stegochat.mapper;

import stegochat.stegochat.dto.NotificationDTO;
import stegochat.stegochat.entity.NotificationsEntity;

public class NotificationMapper {

    // ✅ Convert Entity to DTO (Hides metadata)
    public static NotificationDTO toDTO(NotificationsEntity notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .username(notification.getUsername())
                .type(notification.getType())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .readAt(notification.getReadAt())
                .build();
    }

    // ✅ Convert DTO to Entity (Metadata remains untouched)
    public static NotificationsEntity toEntity(NotificationDTO dto) {
        return NotificationsEntity.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .type(dto.getType())
                .message(dto.getMessage())
                .isRead(dto.isRead())
                .readAt(dto.getReadAt())
                .build();
    }
}
