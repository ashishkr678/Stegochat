package stegochat.stegochat.service;

import stegochat.stegochat.dto.UserDTO;
import java.util.List;

public interface FriendService {

    // ✅ Send a friend request
    void sendFriendRequest(String senderUsername, String receiverUsername);

    // ✅ Accept a friend request
    void acceptFriendRequest(String receiverUsername, String senderUsername);

    // ✅ Reject a friend request
    void rejectFriendRequest(String receiverUsername, String senderUsername);

    // ✅ Remove a friend
    void removeFriend(String username, String friendUsername);

    // ✅ Get a list of friends
    List<UserDTO> getFriends(String username);

    // ✅ Get all pending friend requests
    List<UserDTO> getPendingFriendRequests(String username);
}
