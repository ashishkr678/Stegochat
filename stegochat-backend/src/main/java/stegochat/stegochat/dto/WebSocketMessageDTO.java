package stegochat.stegochat.dto;

import lombok.*;
import stegochat.stegochat.entity.records.MediaData;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessageDTO {
    private String sender;
    private String receiver;
    private String encryptedContent;
    private String messageType;
    private MediaData media;
}
