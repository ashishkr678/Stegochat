package stegochat.stegochat.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stegochat.stegochat.dto.MessageDTO;
import stegochat.stegochat.service.MessageService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // ✅ Get Paginated & Sorted Conversation Between Two Users
    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDTO>> getConversation(
            HttpServletRequest request,
            @RequestParam String otherUser,
            @RequestParam(defaultValue = "0") int page, // Page number (default: 0)
            @RequestParam(defaultValue = "10") int size, // Page size (default: 10 messages)
            @RequestParam(defaultValue = "createdAt") String sortBy, // Sorting field (default: createdAt)
            @RequestParam(defaultValue = "asc") String sortDirection // Sorting direction (asc/desc)
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        List<MessageDTO> conversation = messageService.getConversation(request, otherUser, pageable);
        return ResponseEntity.ok(conversation);
    }

    // ✅ Mark Message as Delivered
    @PostMapping("/delivered/{messageId}")
    public ResponseEntity<Map<String, String>> markAsDelivered(@PathVariable String messageId) {
        messageService.markAsDelivered(messageId);
        return ResponseEntity.ok(Map.of("message", "Message marked as delivered."));
    }

    // ✅ Mark Message as Read
    @PostMapping("/read/{messageId}")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable String messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok(Map.of("message", "Message marked as read."));
    }

    // ✅ Delete Message for Self (`FOR_ME`)
    @DeleteMapping("/delete-for-me/{messageId}")
    public ResponseEntity<Map<String, String>> deleteForMe(HttpServletRequest request, @PathVariable String messageId) {
        messageService.deleteMessageForMe(request, messageId);
        return ResponseEntity.ok(Map.of("message", "Message deleted for you."));
    }

    // ✅ Delete Message for Everyone (`FOR_EVERYONE`)
    @DeleteMapping("/delete-for-everyone/{messageId}")
    public ResponseEntity<Map<String, String>> deleteForEveryone(HttpServletRequest request,
            @PathVariable String messageId) {
        messageService.deleteMessageForEveryone(request, messageId);
        return ResponseEntity.ok(Map.of("message", "Message deleted for everyone."));
    }
}
