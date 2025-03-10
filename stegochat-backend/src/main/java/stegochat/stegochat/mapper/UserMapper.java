package stegochat.stegochat.mapper;

import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.entity.PendingRegistrationEntity;
import stegochat.stegochat.entity.UsersEntity;

public class UserMapper {

    // Convert Entity to DTO
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
                .friends(user.getFriends()) // Read-Only
                .sentRequests(user.getSentRequests()) // Read-Only
                .receivedRequests(user.getReceivedRequests()) // Read-Only
                .build();
    }

    // Convert DTO to Entity (Exclude Friends & Requests to prevent manual updates)
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

    // ✅ NEW METHOD: Convert PendingRegistrationEntity to UsersEntity
    public static UsersEntity toEntity(PendingRegistrationEntity pendingUser) {
        return UsersEntity.builder()
                .username(pendingUser.getUsername())
                .email(pendingUser.getEmail())
                .firstName(pendingUser.getFirstName())
                .lastName(pendingUser.getLastName())
                .phone(pendingUser.getPhone())
                .password(pendingUser.getPassword()) // Already hashed
                .profilePicture(pendingUser.getProfilePicture())
                .about(pendingUser.getAbout())
                .dateOfBirth(pendingUser.getDateOfBirth())
                .build();
    }
}
