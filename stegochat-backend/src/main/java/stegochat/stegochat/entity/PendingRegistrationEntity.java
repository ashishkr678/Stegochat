package stegochat.stegochat.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection = "pending_registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingRegistrationEntity {

    @Id
    private String email;

    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String password;
    private String profilePicture;
    private String about;
    private LocalDate dateOfBirth;

    @Indexed(expireAfterSeconds = 900)
    private LocalDateTime createdAt;
}
