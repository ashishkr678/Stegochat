package stegochat.stegochat.service;

import jakarta.servlet.http.HttpServletRequest;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;
import stegochat.stegochat.dto.MessageDTO;
import java.io.IOException;
import java.util.List;

public interface MessageService {

    List<MessageDTO> getConversation(HttpServletRequest request, String otherUser, Pageable pageable);

    void deleteMessageForMe(HttpServletRequest request, String messageId);

    void deleteMessageForEveryone(HttpServletRequest request, String messageId);

    void deleteChat(HttpServletRequest request, String otherUser);

    ObjectId storeFile(MultipartFile file) throws IOException;

    GridFsResource getFile(String fileId);

    void deleteFile(String fileId);
}
