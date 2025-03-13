package stegochat.stegochat.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import stegochat.stegochat.dto.MessageDTO;
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
import stegochat.stegochat.service.MessageService;
import stegochat.stegochat.service.NotificationService;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final MessageService messageService;
    private final MongoTemplate mongoTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    // Get Paginated & Sorted Conversation Between Two Users
    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDTO>> getConversation(
            HttpServletRequest request,
            @RequestParam String otherUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        List<MessageDTO> conversation = messageService.getConversation(request, otherUser, pageable);
        return ResponseEntity.ok(conversation);
    }

    // Send Message (Real-Time WebSocket)
    @MessageMapping("/chat")
    public void sendMessage(WebSocketMessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        HttpSession session = (HttpSession) headerAccessor.getSessionAttributes().get("session");
        UserDTO senderUser = (session != null) ? (UserDTO) session.getAttribute("userProfile") : null;

        if (senderUser == null) {
            throw new BadRequestException("User session expired. Please reconnect.");
        }

        if (!senderUser.getFriends().contains(message.getReceiver())) {
            throw new BadRequestException("You can only chat with friends.");
        }

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

        String encryptionKey = encryptionKeys.get(message.getReceiver());
        if (encryptionKey == null) {
            throw new BadRequestException("Encryption key not found for this friend.");
        }

        String encryptedContent = (message.getMessageType().equals("TEXT"))
                ? EncryptionUtil.encrypt(message.getEncryptedContent(), encryptionKey)
                : null;

        MessagesEntity messageEntity = MessagesEntity.builder()
                .senderUsername(senderUser.getUsername())
                .receiverUsername(message.getReceiver())
                .messageType(MessageType.valueOf(message.getMessageType()))
                .content(encryptedContent)
                .media(message.getMedia())
                .statusHistory(List.of(new MessageStatusRecord(MessageStatus.SENT, LocalDateTime.now())))
                .build();

        messageRepository.save(messageEntity);

        notificationService.sendNotification(
                message.getReceiver(),
                "New message from " + senderUser.getUsername(),
                NotificationType.MESSAGE,
                messageEntity.getId());
    }

    // Update Message Status (DELIVERED/READ) - WebSocket
    @MessageMapping("/update-status")
    public void updateMessageStatus(List<String> messageIds, String newStatus,
            SimpMessageHeaderAccessor headerAccessor) {

        @SuppressWarnings("null")
        HttpSession session = (HttpSession) headerAccessor.getSessionAttributes().get("session");
        UserDTO user = (session != null) ? (UserDTO) session.getAttribute("userProfile") : null;

        if (user == null) {
            throw new BadRequestException("User session expired. Please reconnect.");
        }

        MessageStatus status = MessageStatus.valueOf(newStatus);
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, MessagesEntity.class);
        Query query = new Query(Criteria.where("id").in(messageIds));
        Update update = new Update().push("statusHistory", new MessageStatusRecord(status, LocalDateTime.now()));

        bulkOps.updateMulti(query, update);
        bulkOps.execute();

        if (status == MessageStatus.READ) {
            notificationService.markNotificationsAsRead(messageIds);

            for (String msgId : messageIds) {
                MessagesEntity msg = messageRepository.findById(msgId).orElse(null);
                if (msg != null) {
                    String receiver = msg.getSenderUsername(); // Notify sender that their message is read
                    messagingTemplate.convertAndSend("/topic/messages/" + receiver,
                            Map.of("messageId", msgId, "status", "READ"));
                }
            }
        }
    }

    // Delete Message for Self (`FOR_ME`)
    @DeleteMapping("/delete-for-me/{messageId}")
    public ResponseEntity<Map<String, String>> deleteForMe(HttpServletRequest request, @PathVariable String messageId) {
        messageService.deleteMessageForMe(request, messageId);
        return ResponseEntity.ok(Map.of("message", "Message deleted for you."));
    }

    // Delete Message for Everyone (`FOR_EVERYONE`)
    @DeleteMapping("/delete-for-everyone/{messageId}")
    public ResponseEntity<Map<String, String>> deleteForEveryone(HttpServletRequest request,
            @PathVariable String messageId) {
        messageService.deleteMessageForEveryone(request, messageId);
        return ResponseEntity.ok(Map.of("message", "Message deleted for everyone."));
    }

    // Delete a Chat (Hides Messages)
    @DeleteMapping("/delete-chat/{otherUser}")
    public ResponseEntity<Map<String, String>> deleteChat(HttpServletRequest request, @PathVariable String otherUser) {
        messageService.deleteChat(request, otherUser);
        return ResponseEntity.ok(Map.of("message", "Chat deleted successfully."));
    }

    // Upload Media File
    @PostMapping("/upload-media")
    public ResponseEntity<Map<String, String>> uploadMedia(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        try {
            ObjectId fileId = messageService.storeFile(file);
            return ResponseEntity.ok(Map.of(
                    "message", "File uploaded successfully",
                    "fileId", fileId.toHexString()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "File upload failed: " + e.getMessage()));
        }
    }

    // Download Media File
    @GetMapping("/download/{fileId}")
    public ResponseEntity<GridFsResource> downloadMedia(@PathVariable String fileId) {
        GridFsResource resource = messageService.getFile(fileId);
        return (resource == null) ? ResponseEntity.notFound().build()
                : ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                        .contentType(MediaType.parseMediaType(resource.getContentType())).body(resource);
    }
}
