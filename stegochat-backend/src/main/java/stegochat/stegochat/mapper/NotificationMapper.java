package stegochat.stegochat.mapper;

import stegochat.stegochat.dto.NotificationDTO;
import stegochat.stegochat.entity.NotificationsEntity;

public class NotificationMapper {

    // Convert Entity to DTO
    public static NotificationDTO toDTO(NotificationsEntity notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .username(notification.getUsername())
                .type(notification.getType())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    // Convert DTO to Entity
    public static NotificationsEntity toEntity(NotificationDTO dto) {
        NotificationsEntity notification = NotificationsEntity.create(
                dto.getUsername(),
                dto.getType(),
                dto.getMessage(),
                null
        );
        notification.setRead(dto.isRead());
        notification.setReadAt(dto.getReadAt());
        return notification;
    }
}
