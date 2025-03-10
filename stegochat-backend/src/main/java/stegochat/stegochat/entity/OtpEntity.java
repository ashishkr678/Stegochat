package stegochat.stegochat.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpEntity {
    @Id
    private String email;
    
    private String otp;
    private String type;

    @Indexed(expireAfterSeconds = 15)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private boolean verified = false;

    public boolean isExpired() {
        return createdAt.plusMinutes(15).isBefore(LocalDateTime.now());
    }
}
