package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import stegochat.stegochat.entity.PendingPasswordResetEntity;

import java.util.Optional;

public interface PendingPasswordResetRepository extends MongoRepository<PendingPasswordResetEntity, String> {
    Optional<PendingPasswordResetEntity> findByEmail(String email);
    void deleteByEmail(String email);
}
