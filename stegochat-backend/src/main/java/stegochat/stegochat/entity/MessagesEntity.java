package stegochat.stegochat.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import stegochat.stegochat.entity.enums.MessageType;
import stegochat.stegochat.entity.records.DeletionRecord;
import stegochat.stegochat.entity.records.MessageStatusRecord;

@Document(collection = "messages")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessagesEntity extends BaseEntity {

    @Id
    private String id;
    
    @Indexed
    private String senderUsername;

    @Indexed
    private String receiverUsername;

    private MessageType messageType;
    private String content;
    private boolean isEdited;
    private boolean isSoftDeleted;
    private boolean isRecalled;

    @Builder.Default
    private List<DeletionRecord> deletionRecords = new ArrayList<>(); // Who deleted & how (for me/everyone)

    @Builder.Default
    private List<MessageStatusRecord> statusHistory = new ArrayList<>(); // SENT, DELIVERED, READ tracking

    // Media Files
    private String mediaFileId;
    private String mediaFileName;
    private long mediaFileSize;
    private String mediaContentType;
    private Long mediaDuration;
    private boolean isStego;
    private String stegoId;

    // ✅ Metadata for Audit Logs (timestamps, edits, etc.)
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
    
}

