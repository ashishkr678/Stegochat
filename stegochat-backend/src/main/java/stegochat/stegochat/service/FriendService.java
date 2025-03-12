package stegochat.stegochat.service;

import jakarta.servlet.http.HttpServletRequest;
import stegochat.stegochat.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface FriendService {

    void sendFriendRequest(HttpServletRequest request, String receiverUsername);
    
    void acceptFriendRequest(HttpServletRequest request, String senderUsername);
    
    void rejectFriendRequest(HttpServletRequest request, String senderUsername);
    
    void removeFriend(HttpServletRequest request, String friendUsername);
    
    List<UserDTO> getFriends(HttpServletRequest request);
    
    List<UserDTO> getPendingFriendRequests(HttpServletRequest request);

    List<UserDTO> getOnlineFriends(HttpServletRequest request);

    boolean isFriendOnline(HttpServletRequest request, String friendUsername);

    LocalDateTime getFriendLastSeen(HttpServletRequest request, String friendUsername);

}
