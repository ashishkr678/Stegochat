package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import stegochat.stegochat.entity.NotificationsEntity;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<NotificationsEntity, String> {

    List<NotificationsEntity> findByUsernameOrderByCreatedAtDesc(String username);

    List<NotificationsEntity> findByUsernameAndIsRead(String username, boolean isRead);

    void deleteByUsername(String username); // ✅ Bulk delete all notifications for a user

    void deleteById(String id); // ✅ Delete a single notification
}
