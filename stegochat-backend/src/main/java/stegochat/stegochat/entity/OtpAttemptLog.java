package stegochat.stegochat.entity;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import stegochat.stegochat.entity.enums.OtpType;

@Document(collection = "otp_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpAttemptLog {
    @Id
    private String id;

    private String email;
    private OtpType type;
    private boolean success;
    private String reason;
    private LocalDateTime timestamp;
}
