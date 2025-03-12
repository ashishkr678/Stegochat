package stegochat.stegochat.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String profilePicture;
    private String about;
    private LocalDate dateOfBirth;
    private Boolean online;
    private LocalDateTime lastSeen;

    private Set<String> friends;
    private Set<String> sentRequests;
    private Set<String> receivedRequests;
}
