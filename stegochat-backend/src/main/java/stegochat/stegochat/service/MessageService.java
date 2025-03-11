package stegochat.stegochat.service;

import jakarta.servlet.http.HttpServletRequest;
import stegochat.stegochat.dto.MessageDTO;
import java.util.List;

import org.springframework.data.domain.Pageable;

public interface MessageService {
    List<MessageDTO> getConversation(HttpServletRequest request, String otherUser, Pageable pageable);

    void markAsDelivered(String messageId);

    void markAsRead(String messageId);

    void deleteMessageForMe(HttpServletRequest request, String messageId);

    void deleteMessageForEveryone(HttpServletRequest request, String messageId);
}
