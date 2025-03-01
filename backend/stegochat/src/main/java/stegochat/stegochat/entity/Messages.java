package stegochat.stegochat.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Messages {
    @Id
    private ObjectId id;  // MongoDB Auto-generated ID

    private String senderUsername;
    private String receiverUsername;

    private MessageType messageType;
    private String content;  // Encrypted message content

    private boolean isEdited;
    private boolean isSoftDeleted;
    private boolean isRecalled;

    @Builder.Default
    private Set<String> deletedForUsers = new HashSet<>(); // Tracks users who deleted it for themselves


    private MessageStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;

    // Media File Handling
    private String mediaFileId; // Reference to GridFS
    private String mediaFileName;
    private long mediaFileSize;
    private String mediaContentType; // image/png, video/mp4, audio/mp3
    private Long mediaDuration; // Duration for audio/video files (in seconds)
    
    private boolean isStego; // Flag to indicate if media is Stego
    private ObjectId stegoId; // Link to Stego entity (Changed from String → ObjectId)

    public enum MessageType {
        TEXT, IMAGE, AUDIO, VIDEO, FILE, STEGO_IMAGE, STEGO_AUDIO
    }

    public enum MessageStatus {
        SENT, DELIVERED, READ
    }
}
