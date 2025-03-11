package stegochat.stegochat.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessageDTO {
    private String sender;
    private String receiver;
    private String encryptedContent;
    private String messageType;
}
