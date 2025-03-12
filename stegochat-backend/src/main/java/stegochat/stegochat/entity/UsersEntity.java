package stegochat.stegochat.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class UsersEntity extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Indexed(unique = true)
    @EqualsAndHashCode.Include
    private String username;

    @Indexed(unique = true)
    private String email;

    private String firstName;
    private String lastName;
    private String phone;

    @JsonIgnore
    private String password;

    private String profilePicture;
    private String about;
    private LocalDate dateOfBirth;

    private boolean online;
    private LocalDateTime lastSeen;  

    @Builder.Default
    private Map<String, String> encryptionKeys = new HashMap<>();

    @Builder.Default
    private Set<String> friends = new HashSet<>();

    @Builder.Default
    private Set<String> sentRequests = new HashSet<>();

    @Builder.Default
    private Set<String> receivedRequests = new HashSet<>();

    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
}
