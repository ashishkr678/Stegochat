package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import stegochat.stegochat.entity.PendingRegistrationEntity;
import java.util.Optional;

@Repository
public interface PendingRegistrationRepository extends MongoRepository<PendingRegistrationEntity, String> {
    
    Optional<PendingRegistrationEntity> findByEmail(String email);

    void deleteByEmail(String email);
    
}
