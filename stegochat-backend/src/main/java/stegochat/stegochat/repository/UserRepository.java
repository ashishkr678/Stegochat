package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import stegochat.stegochat.entity.UsersEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<UsersEntity, String> {

    Optional<UsersEntity> findByUsername(String username);

    Optional<UsersEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<UsersEntity> findAllByUsernameIn(Set<String> friendUsernames);

    List<UsersEntity> findBySentRequestsContaining(String username);

    List<UsersEntity> findByReceivedRequestsContaining(String username);

    boolean existsByUsernameAndFriendsContaining(String username, String friendUsername);
    
}
