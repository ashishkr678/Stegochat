package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import stegochat.stegochat.entity.PendingRegistrationEntity;

import java.util.Optional;

@Repository
public interface PendingRegistrationRepository extends MongoRepository<PendingRegistrationEntity, String> {

    // Find pending registration by email
    Optional<PendingRegistrationEntity> findByEmail(String email);

    // Delete pending registration by email
    void deleteByEmail(String email);

}
