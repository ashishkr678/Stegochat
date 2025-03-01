package stegochat.stegochat.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users extends BaseEntity {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String firstName;
    private String lastName;
    private String phone;
    private String password;
    private String profilePicture;
    private String about;

    @Builder.Default
    private Set<String> friends = new HashSet<>();

    @Builder.Default
    private Set<String> sentRequests = new HashSet<>();

    @Builder.Default
    private Set<String> receivedRequests = new HashSet<>();

    // ✅ Metadata for Enterprise Analytics (e.g., last login, device info)
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
}
