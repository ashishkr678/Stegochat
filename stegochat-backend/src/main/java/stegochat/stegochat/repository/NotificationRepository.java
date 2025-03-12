package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import stegochat.stegochat.entity.NotificationsEntity;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<NotificationsEntity, String> {

    List<NotificationsEntity> findByUsernameOrderByCreatedAtDesc(String username);

    void deleteByUsername(String username);

}
