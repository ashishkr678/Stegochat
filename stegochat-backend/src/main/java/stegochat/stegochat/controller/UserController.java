package stegochat.stegochat.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.security.CookieUtil;
import stegochat.stegochat.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody UserDTO userDTO) {
        userService.initiateRegistration(userDTO);
        return ResponseEntity.ok(Map.of("message", "Registration initiated. OTP sent to " + userDTO.getEmail()));
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<UserDTO> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        if (otp == null || otp.isEmpty()) {
            throw new BadRequestException("OTP is required.");
        }
        UserDTO verifiedUser = userService.completeRegistration(email, otp);
        return ResponseEntity.ok(verifiedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO, HttpServletRequest request,
            HttpServletResponse response) {
        UserDTO userProfile = userService.loginUser(userDTO.getUsername(), userDTO.getPassword(), request, response);
        return ResponseEntity.ok(userProfile);
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }

}
