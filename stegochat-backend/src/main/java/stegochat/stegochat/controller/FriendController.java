package stegochat.stegochat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.dto.UserSummaryDTO;
import stegochat.stegochat.service.FriendService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // Send Friend Request
    @PostMapping("/send-request")
    public ResponseEntity<Map<String, String>> sendFriendRequest(
            HttpServletRequest request,
            @RequestBody Map<String, String> requestBody) {
        
        String receiverUsername = requestBody.get("receiverUsername");
        friendService.sendFriendRequest(request, receiverUsername);
        
        return ResponseEntity.ok(Map.of("message", "Friend request sent successfully."));
    }

    // Accept Friend Request
    @PostMapping("/accept-request")
    public ResponseEntity<Map<String, String>> acceptFriendRequest(
            HttpServletRequest request,
            @RequestBody Map<String, String> requestBody) {
        
        String senderUsername = requestBody.get("senderUsername");
        friendService.acceptFriendRequest(request, senderUsername);
        
        return ResponseEntity.ok(Map.of("message", "Friend request accepted."));
    }

    // Reject Friend Request
    @PostMapping("/reject-request")
    public ResponseEntity<Map<String, String>> rejectFriendRequest(
            HttpServletRequest request,
            @RequestBody Map<String, String> requestBody) {
        
        String senderUsername = requestBody.get("senderUsername");
        friendService.rejectFriendRequest(request, senderUsername);
        
        return ResponseEntity.ok(Map.of("message", "Friend request rejected."));
    }

    // Remove Friend
    @DeleteMapping("/remove-friend")
    public ResponseEntity<Map<String, String>> removeFriend(
            HttpServletRequest request,
            @RequestBody Map<String, String> requestBody) {
        
        String friendUsername = requestBody.get("friendUsername");
        friendService.removeFriend(request, friendUsername);
        
        return ResponseEntity.ok(Map.of("message", "Friend removed successfully."));
    }

    // Cancel requests
    @PostMapping("/cancel-request")
    public ResponseEntity<Map<String, String>> cancelFriendRequest(
            HttpServletRequest request,
            @RequestBody Map<String, String> requestBody) {

        String receiverUsername = requestBody.get("receiverUsername");
        friendService.cancelFriendRequest(request, receiverUsername);

        return ResponseEntity.ok(Map.of("message", "Friend request canceled."));
    }

    // ✅ Get Online Friends
    @GetMapping("/online")
    public ResponseEntity<List<UserSummaryDTO>> getOnlineFriends(HttpServletRequest request) {
        return ResponseEntity.ok(friendService.getOnlineFriends(request));
    }

    // ✅ Check If a Specific Friend Is Online
    @GetMapping("/online/{friendUsername}")
    public ResponseEntity<Boolean> isFriendOnline(HttpServletRequest request, @PathVariable String friendUsername) {
        return ResponseEntity.ok(friendService.isFriendOnline(request, friendUsername));
    }

    // ✅ Get Last Seen of a Friend
    @GetMapping("/lastseen/{friendUsername}")
    public ResponseEntity<LocalDateTime> getFriendLastSeen(HttpServletRequest request,
                                                           @PathVariable String friendUsername) {
        return ResponseEntity.ok(friendService.getFriendLastSeen(request, friendUsername));
    }

    // ✅ Get Friends List
    @GetMapping("/list")
    public ResponseEntity<List<UserSummaryDTO>> getFriends(HttpServletRequest request) {
        return ResponseEntity.ok(friendService.getFriends(request));
    }

    // ✅ Get Pending Friend Requests
    @GetMapping("/pending-requests")
    public ResponseEntity<List<UserSummaryDTO>> getPendingFriendRequests(HttpServletRequest request) {
        return ResponseEntity.ok(friendService.getPendingFriendRequests(request));
    }
}
