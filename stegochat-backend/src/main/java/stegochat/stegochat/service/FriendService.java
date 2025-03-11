package stegochat.stegochat.service;

import stegochat.stegochat.dto.UserDTO;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

public interface FriendService {

    // ✅ Send a friend request
    void sendFriendRequest(HttpServletRequest request, String receiverUsername);

    // ✅ Accept a friend request
    void acceptFriendRequest(HttpServletRequest request, String senderUsername);

    // ✅ Reject a friend request
    public void rejectFriendRequest(HttpServletRequest request, String senderUsername);

    // ✅ Remove a friend
    public void removeFriend(HttpServletRequest request, String friendUsername);

    // ✅ Get a list of friends
    public List<UserDTO> getFriends(HttpServletRequest request);

    // ✅ Get all pending friend requests
    public List<UserDTO> getPendingFriendRequests(HttpServletRequest request);
}
