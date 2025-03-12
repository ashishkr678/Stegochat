package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import stegochat.stegochat.entity.OtpEntity;
import java.util.List;
import java.util.Optional;
import stegochat.stegochat.entity.enums.OtpType;

public interface OtpRepository extends MongoRepository<OtpEntity, String> {

    Optional<OtpEntity> findByEmailAndType(String email, OtpType type);

    void deleteByEmailAndType(String email, OtpType type);

    List<OtpEntity> findByEmail(String email);
}
