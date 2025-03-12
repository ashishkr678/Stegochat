package stegochat.stegochat.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "pending_password_resets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingPasswordResetEntity {

    @Id
    private String email;

    @Indexed(expireAfterSeconds = 900)
    private LocalDateTime createdAt;

    private boolean verified;
}
