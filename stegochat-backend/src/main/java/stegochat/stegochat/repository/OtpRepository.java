package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import stegochat.stegochat.entity.OtpEntity;
import java.util.Optional;

public interface OtpRepository extends MongoRepository<OtpEntity, String> {
    
    Optional<OtpEntity> findByEmailAndType(String email, String type);
        
    void deleteByEmailAndType(String email, String type);

}
