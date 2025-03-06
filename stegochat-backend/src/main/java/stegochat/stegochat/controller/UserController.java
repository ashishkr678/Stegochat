package stegochat.stegochat.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.mapper.UserMapper;
import stegochat.stegochat.security.CookieUtil;
import stegochat.stegochat.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Step 1: Register User and Send OTP
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        userService.initiateRegistration(request, userDTO);
        request.getSession().setAttribute("pendingEmail", userDTO.getEmail()); // Store email instead of username
        return ResponseEntity
                .ok(Map.of("message", "OTP sent to " + userDTO.getEmail() + ". Verify to complete registration."));
    }

    // Step 2: Verify OTP and Complete Registration
    @PostMapping("/register/verify-otp")
    public ResponseEntity<UserDTO> verifyRegistrationOtp(HttpServletRequest request,
            @RequestBody Map<String, Integer> requestBody) {
        Integer otp = requestBody.get("otp");

        UsersEntity registeredUser = userService.completeRegistration(request, otp);

        // Convert UsersEntity to UserDTO to exclude sensitive fields
        UserDTO userDTO = UserMapper.toDTO(registeredUser);

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO, HttpServletRequest request,
            HttpServletResponse response) {
        userService.loginUser(userDTO.getUsername(), userDTO.getPassword(), request, response);
        return ResponseEntity.ok(Map.of("message", "Login successful."));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getUserProfile(request));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request,
            HttpServletRequest httpServletRequest) {
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Bad Request",
                    "message", "New password cannot be empty!"));
        }

        userService.changePassword(httpServletRequest, currentPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
    }

    @PutMapping("/update-phone")
    public ResponseEntity<?> updatePhoneNumber(@RequestBody Map<String, String> request,
            HttpServletRequest httpServletRequest) {
        String newPhoneNumber = request.get("newPhoneNumber");

        if (newPhoneNumber == null || newPhoneNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Bad Request",
                    "message", "New phone number cannot be empty!"));
        }

        userService.updatePhoneNumber(httpServletRequest, newPhoneNumber);
        return ResponseEntity.ok(Map.of("message", "Phone number updated successfully."));
    }

    @PostMapping("/update-email")
    public ResponseEntity<Map<String, String>> updateEmail(
            HttpServletRequest request,
            @RequestBody Map<String, Object> requestBody,
            @AuthenticationPrincipal UserDetails userDetails) {

        String newEmail = (String) requestBody.get("email");
        Integer otp = (Integer) requestBody.get("otp");

        if (newEmail == null || otp == null) {
            throw new IllegalArgumentException("New email and OTP are required!");
        }

        userService.updateEmail(request, newEmail, otp);

        return ResponseEntity.ok(Map.of("message", "Email updated successfully."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not logged in."));
        }

        userService.logout(username, request, response);

        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }

}
