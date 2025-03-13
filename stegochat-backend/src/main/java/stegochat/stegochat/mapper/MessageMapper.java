package stegochat.stegochat.mapper;

import stegochat.stegochat.dto.MessageDTO;
import stegochat.stegochat.entity.MessagesEntity;
import stegochat.stegochat.entity.enums.MessageStatus;
import stegochat.stegochat.entity.records.MessageStatusRecord;

import java.util.List;

public class MessageMapper {

    // ✅ Convert Entity to DTO
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
                .latestStatus(getLatestStatus(message.getStatusHistory())) 
                .media(message.getMedia())  
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
                .media(dto.getMedia())
                .build();
    }

    // ✅ Extract latest status
    private static MessageStatus getLatestStatus(List<MessageStatusRecord> statusHistory) {
        if (statusHistory == null || statusHistory.isEmpty()) {
            return MessageStatus.SENT;
        }
        return statusHistory.get(statusHistory.size() - 1).status(); 
    }
}
