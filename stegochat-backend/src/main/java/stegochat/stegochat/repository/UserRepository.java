package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import stegochat.stegochat.entity.UsersEntity;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UsersEntity, String> {

    // Find user by username
    Optional<UsersEntity> findByUsername(String username);

    // Find user by email
    Optional<UsersEntity> findByEmail(String username);

    // Check if a username exists
    boolean existsByUsername(String username);

    // Check if an email exists
    boolean existsByEmail(String email);
}
