package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import stegochat.stegochat.entity.OtpAttemptLog;
import java.util.List;

public interface OtpAttemptLogRepository extends MongoRepository<OtpAttemptLog, String> {

    List<OtpAttemptLog> findByEmailOrderByTimestampDesc(String email);
}
