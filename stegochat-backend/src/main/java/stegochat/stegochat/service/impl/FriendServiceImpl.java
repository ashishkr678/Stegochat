package stegochat.stegochat.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.entity.enums.NotificationType;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.exception.ResourceNotFoundException;
import stegochat.stegochat.mapper.UserMapper;
import stegochat.stegochat.repository.UserRepository;
import stegochat.stegochat.security.CookieUtil;
import stegochat.stegochat.security.EncryptionUtil;
import stegochat.stegochat.service.FriendService;
import stegochat.stegochat.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final NotificationService notificationService;

    // ✅ Send Friend Request & Trigger Notification
    @Override
    @Transactional
    public void sendFriendRequest(HttpServletRequest request, String receiverUsername) {
        String senderUsername = CookieUtil.extractUsernameFromCookie(request);

        if (senderUsername.equals(receiverUsername)) {
            throw new BadRequestException("You cannot send a friend request to yourself.");
        }

        if (userRepository.existsByUsernameAndFriendsContaining(senderUsername, receiverUsername)) {
            throw new BadRequestException("You are already friends.");
        }

        List<UsersEntity> sentRequests = userRepository.findBySentRequestsContaining(senderUsername);
        if (sentRequests.stream().anyMatch(user -> user.getUsername().equals(receiverUsername))) {
            throw new BadRequestException("Friend request already sent.");
        }

        LocalDateTime now = LocalDateTime.now();

        // ✅ Bulk Update for efficiency
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, UsersEntity.class);

        bulkOps.updateOne(
                Query.query(Criteria.where("username").is(senderUsername)),
                new Update()
                        .addToSet("sentRequests", receiverUsername)
                        .set("metadata.friendRequestsSent." + receiverUsername, now.toString()));

        bulkOps.updateOne(
                Query.query(Criteria.where("username").is(receiverUsername)),
                new Update()
                        .addToSet("receivedRequests", senderUsername)
                        .set("metadata.friendRequestsReceived." + senderUsername, now.toString()));

        bulkOps.execute();

        // ✅ Create & Send Real-Time Notification
        notificationService.createNotification(receiverUsername, "New friend request from " + senderUsername,
                NotificationType.FRIEND_REQUEST, senderUsername);
    }

    // ✅ Accept Friend Request & Trigger Notification
    @Override
    @Transactional
    public void acceptFriendRequest(HttpServletRequest request, String senderUsername) {
        String receiverUsername = CookieUtil.extractUsernameFromCookie(request);

        List<UsersEntity> receivedRequests = userRepository.findByReceivedRequestsContaining(receiverUsername);
        if (receivedRequests.stream().noneMatch(user -> user.getUsername().equals(senderUsername))) {
            throw new BadRequestException("No friend request found from this user.");
        }

        // ✅ Generate Encryption Keys
        String senderKey = EncryptionUtil.generateFriendEncryptionKey(senderUsername, receiverUsername);
        String receiverKey = EncryptionUtil.generateFriendEncryptionKey(receiverUsername, senderUsername);

        LocalDateTime now = LocalDateTime.now();

        // ✅ Bulk Update
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, UsersEntity.class);

        bulkOps.updateOne(
                Query.query(Criteria.where("username").is(senderUsername)),
                new Update()
                        .addToSet("friends", receiverUsername)
                        .pull("sentRequests", receiverUsername)
                        .set("encryptionKeys." + receiverUsername, senderKey)
                        .set("metadata.friendRequestsAccepted." + receiverUsername, now.toString()));

        bulkOps.updateOne(
                Query.query(Criteria.where("username").is(receiverUsername)),
                new Update()
                        .addToSet("friends", senderUsername)
                        .pull("receivedRequests", senderUsername)
                        .set("encryptionKeys." + senderUsername, receiverKey)
                        .set("metadata.friendRequestsAccepted." + senderUsername, now.toString()));

        bulkOps.execute();

        // ✅ Create & Send Real-Time Notification
        notificationService.createNotification(senderUsername, receiverUsername + " accepted your friend request!",
                NotificationType.FRIEND_REQUEST_ACCEPTED, receiverUsername);
    }

    // ✅ Reject Friend Request
    @Override
    @Transactional
    public void rejectFriendRequest(HttpServletRequest request, String senderUsername) {
        String receiverUsername = CookieUtil.extractUsernameFromCookie(request);

        // ✅ Check if request exists
        List<UsersEntity> receivedRequests = userRepository.findByReceivedRequestsContaining(receiverUsername);
        if (receivedRequests.stream().noneMatch(user -> user.getUsername().equals(senderUsername))) {
            throw new BadRequestException("No friend request found from this user.");
        }

        LocalDateTime now = LocalDateTime.now();

        // ✅ Bulk Update
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, UsersEntity.class);

        bulkOps.updateOne(
                Query.query(Criteria.where("username").is(senderUsername)),
                new Update()
                        .pull("sentRequests", receiverUsername)
                        .set("metadata.friendRequestsRejected." + receiverUsername, now.toString()));

        bulkOps.updateOne(
                Query.query(Criteria.where("username").is(receiverUsername)),
                new Update()
                        .pull("receivedRequests", senderUsername)
                        .set("metadata.friendRequestsRejected." + senderUsername, now.toString()));

        bulkOps.execute();
    }

    // ✅ Remove Friend
    @Override
    @Transactional
    public void removeFriend(HttpServletRequest request, String friendUsername) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        if (!userRepository.existsByUsernameAndFriendsContaining(username, friendUsername)) {
            throw new BadRequestException("You are not friends with this user.");
        }

        LocalDateTime now = LocalDateTime.now();

        // ✅ Bulk Update
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, UsersEntity.class);

        bulkOps.updateOne(
                Query.query(Criteria.where("username").is(username)),
                new Update()
                        .pull("friends", friendUsername)
                        .unset("encryptionKeys." + friendUsername)
                        .set("metadata.friendRemoved." + friendUsername, now.toString()));

        bulkOps.updateOne(
                Query.query(Criteria.where("username").is(friendUsername)),
                new Update()
                        .pull("friends", username)
                        .unset("encryptionKeys." + username)
                        .set("metadata.friendRemoved." + username, now.toString()));

        bulkOps.execute();
    }

    // ✅ Get Friends List
    @Override
    public List<UserDTO> getFriends(HttpServletRequest request) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        Set<String> friendUsernames = userRepository.findByUsername(username)
                .map(UsersEntity::getFriends)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return userRepository.findAllByUsernameIn(friendUsernames)
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get Pending Friend Requests
    @Override
    public List<UserDTO> getPendingFriendRequests(HttpServletRequest request) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        List<UsersEntity> pendingRequests = userRepository.findByReceivedRequestsContaining(username);

        return pendingRequests.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
}
