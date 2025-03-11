package stegochat.stegochat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import stegochat.stegochat.entity.MessagesEntity;

@Repository
public interface MessageRepository extends MongoRepository<MessagesEntity, String> {

        Page<MessagesEntity> findBySenderUsernameOrReceiverUsernameOrderByCreatedAt(
                        String senderUsername, String receiverUsername, Pageable pageable);

}
