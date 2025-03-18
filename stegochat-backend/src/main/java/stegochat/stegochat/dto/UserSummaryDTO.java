package stegochat.stegochat.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String about;
    private String profilePicture;
    private boolean online;
    private LocalDateTime lastSeen; 
}
