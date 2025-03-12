package stegochat.stegochat.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserEmailUpdateController {

    private final UserService userService;

    @PostMapping("/update-email")
    public ResponseEntity<Map<String, String>> updateEmail(HttpServletRequest request,
            @RequestBody Map<String, String> body) {
        String newEmail = body.get("newEmail");

        userService.initiateEmailUpdate(request, newEmail);
        return ResponseEntity.ok(Map.of("message", "OTP sent to " + newEmail));
    }

    @PostMapping("/verify-email-update")
    public ResponseEntity<Map<String, String>> verifyEmailUpdate(HttpServletRequest request,
            @RequestBody Map<String, Object> body) {
        Object otpObj = body.get("otp");

        if (otpObj == null) {
            throw new BadRequestException("OTP is required.");
        }

        String otp = String.valueOf(otpObj); // ✅ Convert Integer OTP to String

        userService.verifyEmailUpdate(request, otp);
        return ResponseEntity.ok(Map.of("message", "Email updated successfully."));
    }

}
