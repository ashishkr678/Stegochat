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
import stegochat.stegochat.dto.UserSummaryDTO;
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
import jakarta.servlet.http.HttpSession;

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

        // Send Friend Request
        @Override
        @Transactional
        public void sendFriendRequest(HttpServletRequest request, String receiverUsername) {
                String senderUsername = CookieUtil.extractUsernameFromCookie(request);

                if (senderUsername.equals(receiverUsername)) {
                        throw new BadRequestException("You cannot send a friend request to yourself.");
                }

                UsersEntity sender = userRepository.findByUsername(senderUsername)
                                .orElseThrow(() -> new ResourceNotFoundException("Sender not found."));

                if (sender.getFriends().contains(receiverUsername)) {
                        throw new BadRequestException("You are already friends.");
                }
                if (sender.getSentRequests().contains(receiverUsername)) {
                        throw new BadRequestException("Friend request already sent.");
                }

                LocalDateTime now = LocalDateTime.now();

                BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, UsersEntity.class);
                bulkOps.updateOne(
                                Query.query(Criteria.where("username").is(senderUsername)),
                                new Update()
                                                .addToSet("sentRequests", receiverUsername)
                                                .set("metadata.friendRequestsSent." + receiverUsername, now));

                bulkOps.updateOne(
                                Query.query(Criteria.where("username").is(receiverUsername)),
                                new Update()
                                                .addToSet("receivedRequests", senderUsername)
                                                .set("metadata.friendRequestsReceived." + senderUsername, now));

                bulkOps.execute();

                notificationService.sendNotification(
                                receiverUsername,
                                "New friend request from " + senderUsername,
                                NotificationType.FRIEND_REQUEST,
                                senderUsername);

        }

        // Accept Friend Request
        @Override
        @Transactional
        public void acceptFriendRequest(HttpServletRequest request, String senderUsername) {
                String receiverUsername = CookieUtil.extractUsernameFromCookie(request);

                UsersEntity receiver = userRepository.findByUsername(receiverUsername)
                                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found."));

                if (!receiver.getReceivedRequests().contains(senderUsername)) {
                        throw new BadRequestException("No friend request found from this user.");
                }

                String receiverKey = EncryptionUtil.generateFriendEncryptionKey(receiverUsername, senderUsername);
                String senderKey = EncryptionUtil.generateFriendEncryptionKey(senderUsername, receiverUsername);

                LocalDateTime now = LocalDateTime.now();

                BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, UsersEntity.class);

                bulkOps.updateOne(
                                Query.query(Criteria.where("username").is(receiverUsername)),
                                new Update()
                                                .addToSet("friends", senderUsername)
                                                .pull("receivedRequests", senderUsername)
                                                .set("encryptionKeys." + senderUsername, receiverKey)
                                                .set("metadata.friendRequestsAccepted." + senderUsername, now));

                bulkOps.updateOne(
                                Query.query(Criteria.where("username").is(senderUsername)),
                                new Update()
                                                .addToSet("friends", receiverUsername)
                                                .pull("sentRequests", receiverUsername)
                                                .set("encryptionKeys." + receiverUsername, senderKey)
                                                .set("metadata.friendRequestsAccepted." + receiverUsername, now));

                bulkOps.execute();

                notificationService.sendNotification(
                                senderUsername,
                                receiverUsername + " accepted your friend request!",
                                NotificationType.FRIEND_REQUEST_ACCEPTED,
                                receiverUsername);

        }

        // Reject Friend Request
        @Override
        @Transactional
        public void rejectFriendRequest(HttpServletRequest request, String senderUsername) {
                String receiverUsername = CookieUtil.extractUsernameFromCookie(request);

                UsersEntity receiver = userRepository.findByUsername(receiverUsername)
                                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found."));

                if (!receiver.getReceivedRequests().contains(senderUsername)) {
                        throw new BadRequestException("No friend request found from this user.");
                }

                LocalDateTime now = LocalDateTime.now();
                BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, UsersEntity.class);

                bulkOps.updateOne(
                                Query.query(Criteria.where("username").is(senderUsername)),
                                new Update()
                                                .pull("sentRequests", receiverUsername)
                                                .set("metadata.friendRequestsRejected." + receiverUsername, now));

                bulkOps.updateOne(
                                Query.query(Criteria.where("username").is(receiverUsername)),
                                new Update()
                                                .pull("receivedRequests", senderUsername)
                                                .set("metadata.friendRequestsRejected." + senderUsername, now));

                bulkOps.execute();

        }

        // Remove Friend
        @Override
        @Transactional
        public void removeFriend(HttpServletRequest request, String friendUsername) {
                String username = CookieUtil.extractUsernameFromCookie(request);

                UsersEntity user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

                if (!user.getFriends().contains(friendUsername)) {
                        throw new BadRequestException("You are not friends with this user.");
                }

                LocalDateTime now = LocalDateTime.now();
                BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, UsersEntity.class);

                bulkOps.updateOne(
                                Query.query(Criteria.where("username").is(username)),
                                new Update()
                                                .pull("friends", friendUsername)
                                                .unset("encryptionKeys." + friendUsername)
                                                .set("metadata.friendRemoved." + friendUsername, now));

                bulkOps.updateOne(
                                Query.query(Criteria.where("username").is(friendUsername)),
                                new Update()
                                                .pull("friends", username)
                                                .unset("encryptionKeys." + username)
                                                .set("metadata.friendRemoved." + username, now));

                bulkOps.execute();

                UserDTO updatedUserDTO = UserMapper.toDTO(user);
                HttpSession session = request.getSession(false);
                if (session != null) {
                        session.setAttribute("userProfile", updatedUserDTO);
                }

        }

        // Get Online Friends
        @Override
        public List<UserSummaryDTO> getOnlineFriends(HttpServletRequest request) {

                String username = CookieUtil.extractUsernameFromCookie(request);

                UsersEntity user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

                Set<String> friendUsernames = user.getFriends();

                return userRepository.findByUsernameIn(friendUsernames)
                                .stream()
                                .filter(UsersEntity::isOnline)
                                .sorted((u1, u2) -> u1.getUsername().compareToIgnoreCase(u2.getUsername()))
                                .map(UserMapper::toSummaryDTO) // ✅ Return only required fields
                                .collect(Collectors.toList());
        }

        // Get Specific online friend
        @Override
        public boolean isFriendOnline(HttpServletRequest request, String friendUsername) {

                String username = CookieUtil.extractUsernameFromCookie(request);

                UsersEntity user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

                if (!user.getFriends().contains(friendUsername)) {
                        throw new BadRequestException("This user is not in your friend list.");
                }

                UsersEntity friend = userRepository.findByUsername(friendUsername)
                                .orElseThrow(() -> new ResourceNotFoundException("Friend not found."));

                return friend.isOnline();
        }

        // Get Last Seen
        @Override
        public LocalDateTime getFriendLastSeen(HttpServletRequest request, String friendUsername) {

                String username = CookieUtil.extractUsernameFromCookie(request);

                UsersEntity user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

                if (!user.getFriends().contains(friendUsername)) {
                        throw new ResourceNotFoundException("This user is not in your friend list.");
                }

                return userRepository.findByUsername(friendUsername)
                                .map(UsersEntity::getLastSeen)
                                .orElseThrow(() -> new ResourceNotFoundException("Friend not found."));
        }

        // Get Friends List (Sorted Alphabetically)
        @Override
        public List<UserSummaryDTO> getFriends(HttpServletRequest request) {
                String username = CookieUtil.extractUsernameFromCookie(request);

                UsersEntity user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

                return userRepository.findByUsernameIn(user.getFriends())
                                .stream()
                                .sorted((u1, u2) -> u1.getUsername().compareToIgnoreCase(u2.getUsername()))
                                .map(UserMapper::toSummaryDTO) // ✅ Return only required fields
                                .collect(Collectors.toList());
        }

        // Get Pending Friend Requests (Sorted by Date)
        @Override
        public List<UserSummaryDTO> getPendingFriendRequests(HttpServletRequest request) {
                String username = CookieUtil.extractUsernameFromCookie(request);

                return userRepository.findByReceivedRequestsContaining(username)
                                .stream()
                                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                                .map(UserMapper::toSummaryDTO)
                                .collect(Collectors.toList());
        }
            
}
