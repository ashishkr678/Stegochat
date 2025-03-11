package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import stegochat.stegochat.entity.UsersEntity;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<UsersEntity, String> {

    Optional<UsersEntity> findByUsername(String username);

    Optional<UsersEntity> findByEmail(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UsersEntity> findAllByUsernameIn(Set<String> friendUsernames);
    
}
