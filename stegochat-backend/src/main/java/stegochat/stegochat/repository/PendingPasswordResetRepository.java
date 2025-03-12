package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import stegochat.stegochat.entity.PendingPasswordResetEntity;
import java.util.Optional;

public interface PendingPasswordResetRepository extends MongoRepository<PendingPasswordResetEntity, String> {
    
    Optional<PendingPasswordResetEntity> findByEmailAndVerifiedFalse(String email);
    
    Optional<PendingPasswordResetEntity> findByEmailAndVerifiedTrue(String email);
    
    void deleteByEmail(String email);
}
