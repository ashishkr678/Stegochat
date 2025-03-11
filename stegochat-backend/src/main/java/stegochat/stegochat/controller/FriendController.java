package stegochat.stegochat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.service.FriendService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // ✅ Send Friend Request (No need to pass senderUsername, extracted from session)
    @PostMapping("/send-request")
    public ResponseEntity<Map<String, String>> sendFriendRequest(HttpServletRequest request,
                                                                 @RequestParam String receiverUsername) {
        friendService.sendFriendRequest(request, receiverUsername);
        return ResponseEntity.ok(Map.of("message", "Friend request sent successfully."));
    }

    // ✅ Accept Friend Request
    @PostMapping("/accept-request")
    public ResponseEntity<Map<String, String>> acceptFriendRequest(HttpServletRequest request,
                                                                   @RequestParam String senderUsername) {
        friendService.acceptFriendRequest(request, senderUsername);
        return ResponseEntity.ok(Map.of("message", "Friend request accepted."));
    }

    // ✅ Reject Friend Request
    @PostMapping("/reject-request")
    public ResponseEntity<Map<String, String>> rejectFriendRequest(HttpServletRequest request,
                                                                   @RequestParam String senderUsername) {
        friendService.rejectFriendRequest(request, senderUsername);
        return ResponseEntity.ok(Map.of("message", "Friend request rejected."));
    }

    // ✅ Remove Friend
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, String>> removeFriend(HttpServletRequest request,
                                                            @RequestParam String friendUsername) {
        friendService.removeFriend(request, friendUsername);
        return ResponseEntity.ok(Map.of("message", "Friend removed successfully."));
    }

    // ✅ Get Friends List
    @GetMapping("/list")
    public ResponseEntity<List<UserDTO>> getFriends(HttpServletRequest request) {
        return ResponseEntity.ok(friendService.getFriends(request));
    }

    // ✅ Get Pending Friend Requests
    @GetMapping("/pending-requests")
    public ResponseEntity<List<UserDTO>> getPendingFriendRequests(HttpServletRequest request) {
        return ResponseEntity.ok(friendService.getPendingFriendRequests(request));
    }
}
