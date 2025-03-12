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

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> request) {
        String username = request.get("username");

        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is required."));
        }

        forgotPasswordService.sendOtpForPasswordReset(username);
        return ResponseEntity.ok(Map.of("message", "OTP sent to registered email."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody Map<String, Object> request) {
        Object otpObj = request.get("otp");
        if (otpObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "OTP is required."));
        }

        String otp = String.valueOf(otpObj); // ✅ Convert OTP to String
        forgotPasswordService.verifyOtpForPasswordReset(otp);
        return ResponseEntity.ok(Map.of("message", "OTP verified successfully."));
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "New password is required."));
        }

        forgotPasswordService.resetPassword(newPassword);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
    }
}
