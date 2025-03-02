package stegochat.stegochat.mapper;

import stegochat.stegochat.dto.MessageDTO;
import stegochat.stegochat.entity.MessagesEntity;
import stegochat.stegochat.entity.enums.MessageStatus;
import stegochat.stegochat.entity.records.MessageStatusRecord;

import java.util.List;

public class MessageMapper {

    // ✅ Convert Entity to DTO (Hide unnecessary data)
    public static MessageDTO toDTO(MessagesEntity message) {
        return MessageDTO.builder()
                .id(message.getId())
                .senderUsername(message.getSenderUsername())
                .receiverUsername(message.getReceiverUsername())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .isEdited(message.isEdited())
                .isSoftDeleted(message.isSoftDeleted())
                .isRecalled(message.isRecalled())
                .latestStatus(getLatestStatus(message.getStatusHistory()))  // ✅ Show only latest status
                .mediaFileId(message.getMediaFileId())
                .mediaFileName(message.getMediaFileName())
                .mediaFileSize(message.getMediaFileSize())
                .mediaContentType(message.getMediaContentType())
                .mediaDuration(message.getMediaDuration())
                .isStego(message.isStego())
                .createdAt(message.getCreatedAt())
                .build();
    }

    // ✅ Convert DTO to Entity
    public static MessagesEntity toEntity(MessageDTO dto) {
        return MessagesEntity.builder()
                .id(dto.getId())
                .senderUsername(dto.getSenderUsername())
                .receiverUsername(dto.getReceiverUsername())
                .messageType(dto.getMessageType())
                .content(dto.getContent())
                .isEdited(dto.isEdited())
                .isSoftDeleted(dto.isSoftDeleted())
                .isRecalled(dto.isRecalled())
                .mediaFileId(dto.getMediaFileId())
                .mediaFileName(dto.getMediaFileName())
                .mediaFileSize(dto.getMediaFileSize())
                .mediaContentType(dto.getMediaContentType())
                .mediaDuration(dto.getMediaDuration())
                .isStego(dto.isStego())
                .build();
    }

    // ✅ Extract latest status from statusHistory
    private static MessageStatus getLatestStatus(List<MessageStatusRecord> statusHistory) {
        if (statusHistory == null || statusHistory.isEmpty()) {
            return MessageStatus.SENT;  // Default status if no history
        }
        return statusHistory.get(statusHistory.size() - 1).status(); // Last record = latest status
    }
}
