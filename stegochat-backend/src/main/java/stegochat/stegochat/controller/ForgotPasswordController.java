package stegochat.stegochat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stegochat.stegochat.service.ForgotPasswordService;

import java.util.Map;

@RestController
@RequestMapping("/api/users/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    // Send OTP
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> request) {
        String username = request.get("username");

        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is required."));
        }

        forgotPasswordService.sendOtpForPasswordReset(username);
        return ResponseEntity.ok(Map.of("message", "OTP sent to registered email."));
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String otp = request.get("otp");

        if (username == null || username.isEmpty() || otp == null || otp.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and OTP are required."));
        }

        forgotPasswordService.verifyOtpForPasswordReset(username, otp);
        return ResponseEntity.ok(Map.of("message", "OTP verified successfully."));
    }

    // Reset Password
    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String newPassword = request.get("newPassword");

        if (username == null || username.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and new password are required."));
        }

        forgotPasswordService.resetPassword(username, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
    }
}
