package stegochat.stegochat.dto;

import lombok.*;
import stegochat.stegochat.entity.enums.MessageType;
import stegochat.stegochat.entity.enums.MessageStatus;
import stegochat.stegochat.entity.records.MediaData;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private String id;
    private String senderUsername;
    private String receiverUsername;
    private MessageType messageType;
    private String content;
    private boolean isEdited;
    private boolean isSoftDeleted;
    private boolean isRecalled;
    private MessageStatus latestStatus;  
    private MediaData media;
    private LocalDateTime createdAt;
}
