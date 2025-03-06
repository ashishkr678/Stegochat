package stegochat.stegochat.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private String profilePicture;
    private String about;
    private LocalDate dateOfBirth;

    // Read-Only Friend Lists
    private Set<String> friends;
    private Set<String> sentRequests;
    private Set<String> receivedRequests;

}
