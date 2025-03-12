package stegochat.stegochat.mapper;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.entity.PendingRegistrationEntity;
import stegochat.stegochat.entity.UsersEntity;

public class UserMapper {

    public static UserDTO toDTO(UsersEntity user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .profilePicture(user.getProfilePicture())
                .about(user.getAbout())
                .dateOfBirth(user.getDateOfBirth())
                .friends(user.getFriends())
                .sentRequests(user.getSentRequests())
                .receivedRequests(user.getReceivedRequests())
                .online(user.isOnline())
                .lastSeen(user.getLastSeen()) 
                .build();
    }

    public static UsersEntity toEntity(UserDTO dto) {
        return UsersEntity.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .profilePicture(dto.getProfilePicture())
                .about(dto.getAbout())
                .dateOfBirth(dto.getDateOfBirth())
                .build();
    }

    public static PendingRegistrationEntity toPendingEntity(UserDTO dto, BCryptPasswordEncoder passwordEncoder) {
        return PendingRegistrationEntity.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .password(passwordEncoder.encode(dto.getPassword()))
                .profilePicture(dto.getProfilePicture())
                .about(dto.getAbout())
                .dateOfBirth(dto.getDateOfBirth())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static UsersEntity toEntity(PendingRegistrationEntity pendingUser) {
        return UsersEntity.builder()
                .username(pendingUser.getUsername())
                .email(pendingUser.getEmail())
                .firstName(pendingUser.getFirstName())
                .lastName(pendingUser.getLastName())
                .phone(pendingUser.getPhone())
                .password(pendingUser.getPassword())
                .profilePicture(pendingUser.getProfilePicture())
                .about(pendingUser.getAbout())
                .dateOfBirth(pendingUser.getDateOfBirth())
                .build();
    }

}
