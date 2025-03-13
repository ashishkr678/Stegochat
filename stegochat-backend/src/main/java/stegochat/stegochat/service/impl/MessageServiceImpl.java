package stegochat.stegochat.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import stegochat.stegochat.dto.MessageDTO;
import stegochat.stegochat.entity.MessagesEntity;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.entity.enums.MessageStatus;
import stegochat.stegochat.entity.records.DeletionRecord;
import stegochat.stegochat.entity.records.MessageStatusRecord;
import stegochat.stegochat.exception.ResourceNotFoundException;
import stegochat.stegochat.mapper.MessageMapper;
import stegochat.stegochat.repository.MessageRepository;
import stegochat.stegochat.repository.UserRepository;
import stegochat.stegochat.security.CookieUtil;
import stegochat.stegochat.security.EncryptionUtil;
import stegochat.stegochat.service.MessageService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final GridFsTemplate gridFsTemplate;
    private final GridFSBucket gridFSBucket;
    private final MongoTemplate mongoTemplate;

    private static final long ENCRYPTION_KEY_TIMEOUT_MS = 30 * 60 * 1000; // 30 minutes

    @Override
    public List<MessageDTO> getConversation(HttpServletRequest request, String otherUser, Pageable pageable) {
        String loggedInUser = CookieUtil.extractUsernameFromCookie(request);
        HttpSession session = request.getSession();

        // ✅ Retrieve encryption keys
        Map<String, String> encryptionKeys = getEncryptionKeys(loggedInUser, session);

        // ✅ Fetch messages in a single optimized query
        Page<MessagesEntity> messagesPage = messageRepository.findBySenderUsernameOrReceiverUsernameOrderByCreatedAt(
                loggedInUser, otherUser, pageable);

        List<String> unreadMessageIds = new ArrayList<>();

        List<MessageDTO> conversation = messagesPage.getContent().stream()
                .filter(message -> !message.isSoftDeleted())
                .map(message -> {
                    MessageDTO dto = decryptMessage(message, loggedInUser, encryptionKeys);

                    // ✅ If the message is already delivered but not read, mark it as read
                    if (!message.getSenderUsername().equals(loggedInUser)) {
                        List<MessageStatusRecord> statusHistory = message.getStatusHistory();
                        if (!statusHistory.isEmpty()
                                && statusHistory.get(statusHistory.size() - 1).status() == MessageStatus.DELIVERED) {
                            unreadMessageIds.add(message.getId());
                        }
                    }
                    return dto;
                })
                .toList();

        // ✅ Batch update messages to READ
        if (!unreadMessageIds.isEmpty()) {
            updateMessageStatusBatch(unreadMessageIds, MessageStatus.READ);
        }

        return conversation;
    }

    private void updateMessageStatusBatch(List<String> messageIds, MessageStatus status) {
        if (messageIds.isEmpty())
            return;

        Query query = new Query(Criteria.where("id").in(messageIds));
        Update update = new Update().push("statusHistory", new MessageStatusRecord(status, LocalDateTime.now()));

        mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, MessagesEntity.class)
                .updateMulti(query, update)
                .execute();
    }

    private Map<String, String> getEncryptionKeys(String username, HttpSession session) {
        long currentTime = System.currentTimeMillis();

        @SuppressWarnings("unchecked")
        Map<String, String> storedKeys = (Map<String, String>) session.getAttribute("encryptionKeys");
        Long lastRefreshTime = (Long) session.getAttribute("encryptionKeysRefreshTime");

        if (storedKeys == null || lastRefreshTime == null
                || (currentTime - lastRefreshTime > ENCRYPTION_KEY_TIMEOUT_MS)) {
            UsersEntity senderUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
            storedKeys = senderUser.getEncryptionKeys();
            session.setAttribute("encryptionKeys", storedKeys);
            session.setAttribute("encryptionKeysRefreshTime", currentTime);
        }
        return storedKeys;
    }

    private MessageDTO decryptMessage(MessagesEntity message, String loggedInUser, Map<String, String> encryptionKeys) {
        MessageDTO dto = MessageMapper.toDTO(message);
        String conversationUser = loggedInUser.equals(message.getSenderUsername()) ? message.getReceiverUsername()
                : message.getSenderUsername();
        String encryptionKey = encryptionKeys.get(conversationUser);

        if (encryptionKey != null) {
            dto.setContent(EncryptionUtil.decrypt(dto.getContent(), encryptionKey));
        } else {
            dto.setContent("[Encrypted Message - Key Missing]");
        }
        return dto;
    }

    @Override
    public void deleteMessageForMe(HttpServletRequest request, String messageId) {
        updateDeletionRecord(request, messageId, "FOR_ME");
    }

    @Override
    public void deleteMessageForEveryone(HttpServletRequest request, String messageId) {
        updateDeletionRecord(request, messageId, "FOR_EVERYONE");
        messageRepository.findById(messageId).ifPresent(message -> {
            message.setSoftDeleted(true);
            message.setRecalled(true);
            messageRepository.save(message);
        });
    }

    private void updateDeletionRecord(HttpServletRequest request, String messageId, String deleteType) {
        String username = CookieUtil.extractUsernameFromCookie(request);
        messageRepository.findById(messageId).ifPresent(message -> {
            message.getDeletionRecords().add(new DeletionRecord(messageId, username, deleteType, LocalDateTime.now()));
            messageRepository.save(message);
        });
    }

    @Override
    public void deleteChat(HttpServletRequest request, String otherUser) {
        String username = CookieUtil.extractUsernameFromCookie(request);
        List<MessagesEntity> messages = messageRepository.findBySenderUsernameOrReceiverUsernameOrderByCreatedAt(
                username, otherUser, Pageable.unpaged()).getContent();

        for (MessagesEntity message : messages) {
            message.getDeletionRecords()
                    .add(new DeletionRecord(message.getId(), username, "CHAT_DELETED", LocalDateTime.now()));
        }
        messageRepository.saveAll(messages);
    }

    @Override
    public ObjectId storeFile(MultipartFile file) throws IOException {
        return gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
    }

    @Override
    public GridFsResource getFile(String fileId) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query()
                .addCriteria(Criteria.where("_id").is(new ObjectId(fileId))));
        return (gridFSFile != null)
                ? new GridFsResource(gridFSFile, gridFSBucket.openDownloadStream(gridFSFile.getObjectId()))
                : null;
    }

    @Override
    public void deleteFile(String fileId) {
        gridFsTemplate.delete(new Query()
                .addCriteria(Criteria.where("_id").is(new ObjectId(fileId))));
    }

}
