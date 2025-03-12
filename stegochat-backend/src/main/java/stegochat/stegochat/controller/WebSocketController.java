package stegochat.stegochat.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.dto.WebSocketMessageDTO;
import stegochat.stegochat.entity.MessagesEntity;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.entity.enums.MessageStatus;
import stegochat.stegochat.entity.enums.MessageType;
import stegochat.stegochat.entity.enums.NotificationType;
import stegochat.stegochat.entity.records.MessageStatusRecord;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.exception.ResourceNotFoundException;
import stegochat.stegochat.repository.MessageRepository;
import stegochat.stegochat.repository.UserRepository;
import stegochat.stegochat.security.EncryptionUtil;

@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final WebSocketNotificationController notificationController;
    private final MongoTemplate mongoTemplate;

    // ✅ Send Message & Trigger Notification
    @MessageMapping("/chat")
    public void sendMessage(WebSocketMessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        HttpSession session = (HttpSession) headerAccessor.getSessionAttributes().get("session");
        UserDTO senderUser = (session != null) ? (UserDTO) session.getAttribute("userProfile") : null;

        if (senderUser == null) {
            throw new BadRequestException("User session expired. Please reconnect.");
        }

        // ✅ Ensure receiver is in sender’s friend list
        if (!senderUser.getFriends().contains(message.getReceiver())) {
            throw new BadRequestException("You can only chat with friends.");
        }

        // ✅ Refresh encryption keys periodically
        Long lastRefreshTime = (Long) session.getAttribute("encryptionKeysRefreshTime");
        long currentTime = System.currentTimeMillis();
        boolean shouldRefreshKeys = (lastRefreshTime == null || (currentTime - lastRefreshTime) > (30 * 60 * 1000));

        @SuppressWarnings("unchecked")
        Map<String, String> encryptionKeys = (Map<String, String>) session.getAttribute("encryptionKeys");

        if (shouldRefreshKeys || encryptionKeys == null) {
            UsersEntity senderEntity = userRepository.findByUsername(senderUser.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Sender not found."));
            encryptionKeys = senderEntity.getEncryptionKeys();

            session.setAttribute("encryptionKeys", encryptionKeys);
            session.setAttribute("encryptionKeysRefreshTime", currentTime);
        }

        // ✅ Get Friend-Specific Encryption Key
        String encryptionKey = encryptionKeys.get(message.getReceiver());
        if (encryptionKey == null) {
            throw new BadRequestException("Encryption key not found for this friend.");
        }

        // ✅ Encrypt and send the message
        String encryptedContent = EncryptionUtil.encrypt(message.getEncryptedContent(), encryptionKey);
        message.setEncryptedContent(encryptedContent);
        message.setSender(senderUser.getUsername());

        MessagesEntity messageEntity = MessagesEntity.builder()
                .senderUsername(senderUser.getUsername())
                .receiverUsername(message.getReceiver())
                .messageType(MessageType.valueOf(message.getMessageType()))
                .content(encryptedContent)
                .statusHistory(List.of(new MessageStatusRecord(MessageStatus.SENT, LocalDateTime.now())))
                .build();

        messageRepository.save(messageEntity);

        // ✅ Trigger a Message Notification after sending the message
        notificationController.sendNotification(
                message.getReceiver(),
                "New message from " + senderUser.getUsername(),
                messageEntity.getId(),
                NotificationType.MESSAGE // ✅ Set correct notification type
        );
    }

    // ✅ Batch Update Message Status (DELIVERED/READ)
    @MessageMapping("/update-status")
    public void updateMessageStatus(List<String> messageIds, String newStatus,
            SimpMessageHeaderAccessor headerAccessor) {
        HttpSession session = (HttpSession) headerAccessor.getSessionAttributes().get("session");
        UserDTO user = (session != null) ? (UserDTO) session.getAttribute("userProfile") : null;

        if (user == null) {
            throw new BadRequestException("User session expired. Please reconnect.");
        }

        // ✅ Validate message status
        MessageStatus status;
        try {
            status = MessageStatus.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid message status.");
        }

        // ✅ Batch update message status
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, MessagesEntity.class);
        Query query = new Query(Criteria.where("id").in(messageIds));
        Update update = new Update().push("statusHistory", new MessageStatusRecord(status, LocalDateTime.now()));

        bulkOps.updateMulti(query, update);
        bulkOps.execute();
    }

}
