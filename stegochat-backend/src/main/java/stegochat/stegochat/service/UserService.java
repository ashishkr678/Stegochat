package stegochat.stegochat.service;

import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.entity.UsersEntity;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    void initiateRegistration(HttpServletRequest request, UserDTO userDTO);

    UsersEntity completeRegistration(HttpServletRequest request, int otp);

    void loginUser(String username, String password, HttpServletRequest request, HttpServletResponse response);

    UserDTO getUserProfile(HttpServletRequest request);

    Optional<UserDTO> getUserByUsername(String username);

    void changePassword(HttpServletRequest request, String currentPassword, String newPassword);

    void updatePhoneNumber(HttpServletRequest request, String newPhoneNumber);

    void updateEmail(HttpServletRequest request, String newEmail, int otp);

    void logout(String username, HttpServletRequest request, HttpServletResponse response);
    
}
