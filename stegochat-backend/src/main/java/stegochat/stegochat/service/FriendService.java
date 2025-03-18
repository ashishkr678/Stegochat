package stegochat.stegochat.service;

import jakarta.servlet.http.HttpServletRequest;
import stegochat.stegochat.dto.UserSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface FriendService {

    void sendFriendRequest(HttpServletRequest request, String receiverUsername);
    
    void acceptFriendRequest(HttpServletRequest request, String senderUsername);
    
    void rejectFriendRequest(HttpServletRequest request, String senderUsername);

    void cancelFriendRequest(HttpServletRequest request, String receiverUsername);
    
    void removeFriend(HttpServletRequest request, String friendUsername);
    
    List<UserSummaryDTO> getFriends(HttpServletRequest request);
    
    List<UserSummaryDTO> getPendingFriendRequests(HttpServletRequest request);

    List<UserSummaryDTO> getOnlineFriends(HttpServletRequest request);

    boolean isFriendOnline(HttpServletRequest request, String friendUsername);

    LocalDateTime getFriendLastSeen(HttpServletRequest request, String friendUsername);

}
