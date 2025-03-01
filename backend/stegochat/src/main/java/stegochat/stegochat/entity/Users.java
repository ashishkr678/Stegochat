package stegochat.stegochat.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    @Id
    private ObjectId id;  // MongoDB Auto-generated Unique ID

    @Indexed(unique = true)
    private String username;  // Unique & Indexed Username (Primary Identifier)

    @Indexed(unique = true)
    private String email;  // Ensures unique email
    
    private String firstName;
    private String lastName;
    private String phone;
    private String password;  // Store as HASHED (e.g., BCrypt)
    private String profilePicture;
    private String about;

    @Builder.Default
    private Set<String> friends = new HashSet<>(); // Friends list for chat

    @Builder.Default
    private Set<String> sentRequests = new HashSet<>(); // Sent friend requests

    @Builder.Default
    private Set<String> receivedRequests = new HashSet<>(); // Received friend requests

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
