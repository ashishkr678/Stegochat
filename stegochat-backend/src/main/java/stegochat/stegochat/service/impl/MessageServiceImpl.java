package stegochat.stegochat.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public List<MessageDTO> getConversation(HttpServletRequest request, String otherUser, Pageable pageable) {
        String loggedInUser = CookieUtil.extractUsernameFromCookie(request);
        HttpSession session = request.getSession();

        final long ENCRYPTION_KEY_TIMEOUT_MS = 30 * 60 * 1000; // 30 minutes
        long currentTime = System.currentTimeMillis();

        // ✅ Retrieve encryption keys from session
        @SuppressWarnings("unchecked")
        Map<String, String> storedKeys = (Map<String, String>) session.getAttribute("encryptionKeys");
        Long lastRefreshTime = (Long) session.getAttribute("encryptionKeysRefreshTime");

        // ✅ Refresh encryption keys if expired or missing
        final Map<String, String> encryptionKeys;
        if (storedKeys == null || lastRefreshTime == null
                || (currentTime - lastRefreshTime > ENCRYPTION_KEY_TIMEOUT_MS)) {
            UsersEntity senderUser = userRepository.findByUsername(loggedInUser)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
            encryptionKeys = senderUser.getEncryptionKeys();

            // ✅ Store updated keys in session
            session.setAttribute("encryptionKeys", encryptionKeys);
            session.setAttribute("encryptionKeysRefreshTime", currentTime);
        } else {
            encryptionKeys = storedKeys; // ✅ Use cached encryption keys
        }

        // ✅ Fetch paginated messages
        Page<MessagesEntity> messagesPage = messageRepository
                .findBySenderUsernameOrReceiverUsernameOrderByCreatedAt(loggedInUser, otherUser, pageable);

        return messagesPage.getContent().stream()
                .filter(message -> !message.isSoftDeleted())
                .map(message -> {
                    MessageDTO dto = MessageMapper.toDTO(message);

                    // ✅ Determine correct encryption key based on sender/receiver
                    String conversationUser = loggedInUser.equals(message.getSenderUsername())
                            ? message.getReceiverUsername()
                            : message.getSenderUsername();

                    String encryptionKey = encryptionKeys.get(conversationUser);

                    // ✅ Handle missing encryption key safely
                    if (encryptionKey != null) {
                        dto.setContent(EncryptionUtil.decrypt(dto.getContent(), encryptionKey));
                    } else {
                        dto.setContent("[Encrypted Message - Key Missing]");
                    }

                    return dto;
                })
                .toList();
    }

    @Override
    public void markAsDelivered(String messageId) {
        Optional<MessagesEntity> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty())
            return;

        MessagesEntity message = messageOpt.get();

        // Ensure it's not already marked as DELIVERED
        List<MessageStatusRecord> statusHistory = message.getStatusHistory();
        if (!statusHistory.isEmpty()
                && statusHistory.get(statusHistory.size() - 1).status() == MessageStatus.DELIVERED) {
            return; // Already marked as DELIVERED
        }

        message.getStatusHistory().add(new MessageStatusRecord(MessageStatus.DELIVERED, LocalDateTime.now()));
        messageRepository.save(message);
    }

    @Override
    public void markAsRead(String messageId) {
        Optional<MessagesEntity> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty())
            return;

        MessagesEntity message = messageOpt.get();

        // Ensure that READ is only marked after DELIVERED
        List<MessageStatusRecord> statusHistory = message.getStatusHistory();
        if (statusHistory.isEmpty()
                || statusHistory.get(statusHistory.size() - 1).status() != MessageStatus.DELIVERED) {
            return; // Message must be delivered before marking as read
        }

        // Ensure it's not already marked as READ
        if (statusHistory.get(statusHistory.size() - 1).status() == MessageStatus.READ) {
            return; // Already marked as READ
        }

        message.getStatusHistory().add(new MessageStatusRecord(MessageStatus.READ, LocalDateTime.now()));
        messageRepository.save(message);
    }

    @Override
    public void deleteMessageForMe(HttpServletRequest request, String messageId) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        Optional<MessagesEntity> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty())
            return;

        MessagesEntity message = messageOpt.get();
        message.getDeletionRecords().add(new DeletionRecord(messageId, username, "FOR_ME", LocalDateTime.now()));

        messageRepository.save(message);
    }

    @Override
    public void deleteMessageForEveryone(HttpServletRequest request, String messageId) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        Optional<MessagesEntity> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty())
            return;

        MessagesEntity message = messageOpt.get();
        message.setSoftDeleted(true);
        message.setRecalled(true);
        message.getDeletionRecords().add(new DeletionRecord(messageId, username, "FOR_EVERYONE", LocalDateTime.now()));

        messageRepository.save(message);
    }
}
