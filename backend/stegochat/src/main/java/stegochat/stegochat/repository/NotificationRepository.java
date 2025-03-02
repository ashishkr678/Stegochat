package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import stegochat.stegochat.entity.NotificationsEntity;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<NotificationsEntity, String> {

    /**
     * Retrieves all notifications for a specific user, ordered by creation time.
     * 
     * @param username The username of the user.
     * @return A list of notifications for the user, sorted by creation time.
     */
    List<NotificationsEntity> findByUsernameOrderByCreatedAt(String username);

    /**
     * Retrieves all unread notifications for a specific user.
     * 
     * @param username The username of the user.
     * @param isRead Whether the notification has been read (false for unread).
     * @return A list of unread notifications for the user.
     */
    List<NotificationsEntity> findByUsernameAndIsRead(String username, boolean isRead);
}
