package stegochat.stegochat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import stegochat.stegochat.entity.MessagesEntity;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<MessagesEntity, String> {

    /**
     * Retrieves all messages exchanged between two users, sorted by creation time.
     * 
     * @param senderUsername  The username of the sender.
     * @param receiverUsername The username of the receiver.
     * @return A list of messages between the sender and receiver, ordered by creation time.
     */
    List<MessagesEntity> findBySenderUsernameAndReceiverUsernameOrderByCreatedAt(
            String senderUsername, String receiverUsername);

    /**
     * Retrieves all messages where a user is either the sender or receiver, sorted by creation time.
     * 
     * @param username The username of the user.
     * @return A list of messages where the user is involved, ordered by creation time.
     */
    List<MessagesEntity> findBySenderUsernameOrReceiverUsernameOrderByCreatedAt(
            String username, String username2);
}
