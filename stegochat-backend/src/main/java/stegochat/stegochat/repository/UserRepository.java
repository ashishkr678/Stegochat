package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.Query;
import stegochat.stegochat.entity.UsersEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<UsersEntity, String> {

    Optional<UsersEntity> findByUsername(String username);
    Optional<UsersEntity> findByEmail(String email);

    Boolean existsByUsername(String username);    

    boolean existsByEmail(String email);

    List<UsersEntity> findByUsernameIn(Set<String> usernames);

    @Query("{ 'sentRequests': ?0 }")
    List<UsersEntity> findBySentRequestsContaining(String username);

    @Query("{ 'receivedRequests': ?0 }")
    List<UsersEntity> findByReceivedRequestsContaining(String username);

    @Query("{ 'username': ?0, 'friends': ?1 }")
    boolean existsByUsernameAndFriendsContaining(String username, String friendUsername);
}
