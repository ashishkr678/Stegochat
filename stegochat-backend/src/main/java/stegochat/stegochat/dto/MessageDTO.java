package stegochat.stegochat.dto;

import lombok.*;
import stegochat.stegochat.entity.enums.MessageType;
import stegochat.stegochat.entity.enums.MessageStatus;

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
    private MessageStatus latestStatus;  // ✅ Only latest status (Sent, Delivered, Read)
    private String mediaFileId;
    private String mediaFileName;
    private long mediaFileSize;
    private String mediaContentType;
    private Long mediaDuration;
    private boolean isStego;
    private LocalDateTime createdAt;
}
