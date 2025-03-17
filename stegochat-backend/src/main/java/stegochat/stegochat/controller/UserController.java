package stegochat.stegochat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import stegochat.stegochat.dto.LoginDTO;
import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.dto.UserSummaryDTO;
import stegochat.stegochat.entity.enums.OtpType;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.exception.ResourceNotFoundException;
import stegochat.stegochat.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Initiate Registration
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody UserDTO userDTO) {
        userService.initiateRegistration(userDTO);
        return ResponseEntity.ok(Map.of("message", "Registration initiated. OTP sent to " + userDTO.getEmail()));
    }

    // Verify Registration OTP
    @PostMapping("/register/verify-otp")
    public ResponseEntity<UserDTO> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || email.isEmpty()) {
            throw new BadRequestException("Email is required.");
        }
        if (otp == null || otp.isEmpty()) {
            throw new BadRequestException("OTP is required.");
        }

        return ResponseEntity.ok(userService.completeRegistration(email, otp));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginDTO loginDTO,
            HttpServletRequest request,
            HttpServletResponse response) {
        UserDTO userProfile = userService.loginUser(loginDTO.getUsername(), loginDTO.getPassword(), request, response);
        return ResponseEntity.ok(Map.of("message", "Login successful", "user", userProfile));
    }

    // Get User Profile
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of("user", userService.getUserProfile(request)));
    }

    // Get User by Username
    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(Map.of("username", userService.getUserByUsername(username)));
    }

    // Search users
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        try {
            List<UserSummaryDTO> users = userService.searchUsersByUsername(query);
            return ResponseEntity.ok(users);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        }
    }

    // Change Password
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> request,
            HttpServletRequest httpServletRequest) {
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new BadRequestException("New password cannot be empty!");
        }

        userService.changePassword(httpServletRequest, currentPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
    }

    // Update Phone Number
    @PutMapping("/update-phone")
    public ResponseEntity<Map<String, String>> updatePhoneNumber(@RequestBody Map<String, String> request,
            HttpServletRequest httpServletRequest) {
        String newPhoneNumber = request.get("newPhoneNumber");

        if (newPhoneNumber == null || newPhoneNumber.trim().isEmpty()) {
            throw new BadRequestException("New phone number cannot be empty!");
        }

        userService.updatePhoneNumber(httpServletRequest, newPhoneNumber);
        return ResponseEntity.ok(Map.of("message", "Phone number updated successfully."));
    }

    // Resend OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<Map<String, String>> resendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String type = request.get("type");

        if (email == null || type == null || email.isEmpty() || type.isEmpty()) {
            throw new BadRequestException("Email and OTP type are required.");
        }

        try {
            OtpType otpType = OtpType.valueOf(type.toUpperCase());
            userService.resendOtp(email, otpType);
            return ResponseEntity.ok(Map.of("message", "OTP resent successfully."));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid OTP type.");
        }
    }

    // Logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser")) {
            return ResponseEntity.ok(Map.of("message", "Authenticated"));
        }
        return ResponseEntity.ok(Map.of("message", "User is not authenticated."));
    }
}
