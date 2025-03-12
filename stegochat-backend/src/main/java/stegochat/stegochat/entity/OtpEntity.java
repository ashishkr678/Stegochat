package stegochat.stegochat.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stegochat.stegochat.entity.enums.OtpType;

@Document(collection = "otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpEntity {
    @Id
    private String id;

    private String email;
    private String otp;
    private OtpType type;

    @Indexed(expireAfterSeconds = 900)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private boolean verified = false;

    private int failedAttempts;

    public boolean isExpired() {
        return createdAt.plusMinutes(15).isBefore(LocalDateTime.now());
    }
}
