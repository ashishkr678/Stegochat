package stegochat.stegochat.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import stegochat.stegochat.entity.enums.MessageType;
import stegochat.stegochat.entity.records.DeletionRecord;
import stegochat.stegochat.entity.records.MediaData;
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

    @Indexed
    private MessageType messageType;

    private String content;
    private boolean isEdited;

    @Indexed
    private boolean isSoftDeleted;

    @Indexed
    private boolean isRecalled;

    @Builder.Default
    private List<DeletionRecord> deletionRecords = new ArrayList<>();

    @Builder.Default
    private List<MessageStatusRecord> statusHistory = new ArrayList<>();

    private MediaData media;

    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
}
