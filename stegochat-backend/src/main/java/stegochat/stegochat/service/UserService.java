package stegochat.stegochat.service;

import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.entity.enums.OtpType;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    void initiateRegistration(UserDTO userDTO);

    UserDTO completeRegistration(String email, String otp);

    UserDTO loginUser(String username, String password, HttpServletRequest request, HttpServletResponse response);

    UserDTO getUserProfile(HttpServletRequest request);

    Optional<String> getUserByUsername(String username);

    void changePassword(HttpServletRequest request, String currentPassword, String newPassword);

    void updatePhoneNumber(HttpServletRequest request, String newPhoneNumber);

    void initiateEmailUpdate(HttpServletRequest request, String newEmail);

    void verifyEmailUpdate(HttpServletRequest request, String otp); 
    
    void resendOtp(String email, OtpType otpType);

    void logout(HttpServletRequest request, HttpServletResponse response);
    
}
