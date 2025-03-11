package stegochat.stegochat.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.exception.ResourceNotFoundException;
import stegochat.stegochat.mapper.UserMapper;
import stegochat.stegochat.repository.UserRepository;
import stegochat.stegochat.security.EncryptionUtil;
import stegochat.stegochat.service.FriendService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final UserRepository userRepository;

    // ✅ Send Friend Request
    @Override
    @Transactional
    public void sendFriendRequest(String senderUsername, String receiverUsername) {
        if (senderUsername.equals(receiverUsername)) {
            throw new BadRequestException("You cannot send a friend request to yourself.");
        }

        UsersEntity sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found."));
        UsersEntity receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found."));

        if (sender.getFriends().contains(receiverUsername)) {
            throw new BadRequestException("You are already friends.");
        }
        if (sender.getSentRequests().contains(receiverUsername)) {
            throw new BadRequestException("Friend request already sent.");
        }

        sender.getSentRequests().add(receiverUsername);
        receiver.getReceivedRequests().add(senderUsername);

        userRepository.save(sender);
        userRepository.save(receiver);
    }

    // ✅ Accept Friend Request
    @Override
    @Transactional
    public void acceptFriendRequest(String receiverUsername, String senderUsername) {
        UsersEntity receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found."));
        UsersEntity sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found."));

        if (!receiver.getReceivedRequests().contains(senderUsername)) {
            throw new BadRequestException("No friend request found from this user.");
        }

        // ✅ Add to friends list
        receiver.getFriends().add(senderUsername);
        sender.getFriends().add(receiverUsername);

        // ✅ Remove from request lists
        receiver.getReceivedRequests().remove(senderUsername);
        sender.getSentRequests().remove(receiverUsername);

        // ✅ Generate Encryption Keys for Secure Chat
        String senderKey = EncryptionUtil.generateFriendEncryptionKey(senderUsername, receiverUsername);
        String receiverKey = EncryptionUtil.generateFriendEncryptionKey(receiverUsername, senderUsername);

        sender.getEncryptionKeys().put(receiverUsername, senderKey);
        receiver.getEncryptionKeys().put(senderUsername, receiverKey);

        userRepository.save(sender);
        userRepository.save(receiver);
    }

    // ✅ Reject Friend Request
    @Override
    @Transactional
    public void rejectFriendRequest(String receiverUsername, String senderUsername) {
        UsersEntity receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found."));
        UsersEntity sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found."));

        if (!receiver.getReceivedRequests().contains(senderUsername)) {
            throw new BadRequestException("No friend request found from this user.");
        }

        // ✅ Remove from request lists
        receiver.getReceivedRequests().remove(senderUsername);
        sender.getSentRequests().remove(receiverUsername);

        userRepository.save(sender);
        userRepository.save(receiver);
    }

    // ✅ Remove a Friend
    @Override
    @Transactional
    public void removeFriend(String username, String friendUsername) {
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        UsersEntity friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Friend not found."));

        if (!user.getFriends().contains(friendUsername)) {
            throw new BadRequestException("You are not friends with this user.");
        }

        user.getFriends().remove(friendUsername);
        friend.getFriends().remove(username);

        // ✅ Remove Encryption Keys
        user.getEncryptionKeys().remove(friendUsername);
        friend.getEncryptionKeys().remove(username);

        userRepository.save(user);
        userRepository.save(friend);
    }

    // ✅ Get Friends List
    @Override
    public List<UserDTO> getFriends(String username) {
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        Set<String> friendUsernames = user.getFriends();

        return userRepository.findAllByUsernameIn(friendUsernames)
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get Pending Friend Requests
    @Override
    public List<UserDTO> getPendingFriendRequests(String username) {
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        Set<String> requestUsernames = user.getReceivedRequests();

        return userRepository.findAllByUsernameIn(requestUsernames)
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
}
