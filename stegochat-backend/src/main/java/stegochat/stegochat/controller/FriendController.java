package stegochat.stegochat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.service.FriendService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // ✅ Send Friend Request
    @PostMapping("/send-request")
    public ResponseEntity<Map<String, String>> sendFriendRequest(@RequestParam String senderUsername,
                                                                 @RequestParam String receiverUsername) {
        friendService.sendFriendRequest(senderUsername, receiverUsername);
        return ResponseEntity.ok(Map.of("message", "Friend request sent successfully."));
    }

    // ✅ Accept Friend Request
    @PostMapping("/accept-request")
    public ResponseEntity<Map<String, String>> acceptFriendRequest(@RequestParam String receiverUsername,
                                                                   @RequestParam String senderUsername) {
        friendService.acceptFriendRequest(receiverUsername, senderUsername);
        return ResponseEntity.ok(Map.of("message", "Friend request accepted."));
    }

    // ✅ Reject Friend Request
    @PostMapping("/reject-request")
    public ResponseEntity<Map<String, String>> rejectFriendRequest(@RequestParam String receiverUsername,
                                                                   @RequestParam String senderUsername) {
        friendService.rejectFriendRequest(receiverUsername, senderUsername);
        return ResponseEntity.ok(Map.of("message", "Friend request rejected."));
    }

    // ✅ Get Friends List
    @GetMapping("/{username}/friends")
    public ResponseEntity<List<UserDTO>> getFriends(@PathVariable String username) {
        return ResponseEntity.ok(friendService.getFriends(username));
    }

    // ✅ Get Pending Friend Requests
    @GetMapping("/{username}/pending-requests")
    public ResponseEntity<List<UserDTO>> getPendingRequests(@PathVariable String username) {
        return ResponseEntity.ok(friendService.getPendingFriendRequests(username));
    }
}
