package stegochat.stegochat.entity;

import java.util.Date;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "pending_password_resets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingPasswordResetEntity {

    @Id
    private String email;

    @Indexed(expireAfterSeconds = 900)
    private Date createdAt;

    private boolean verified;
}
