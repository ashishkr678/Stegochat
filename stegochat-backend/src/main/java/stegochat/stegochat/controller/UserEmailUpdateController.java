package stegochat.stegochat.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/user/email")
@RequiredArgsConstructor
public class UserEmailUpdateController {

    private final UserService userService;

    // Initiate Email Update
    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updateEmail(HttpServletRequest request,
                                                           @RequestBody Map<String, String> body) {
        String newEmail = body.get("newEmail");

        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new BadRequestException("New email is required.");
        }

        userService.initiateEmailUpdate(request, newEmail);
        return ResponseEntity.ok(Map.of("message", "OTP sent to " + newEmail));
    }

    // Verify Email Update
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyEmailUpdate(HttpServletRequest request,
                                                                 @RequestBody Map<String, Object> body) {
        Object otpObj = body.get("otp");

        if (otpObj == null) {
            throw new BadRequestException("OTP is required.");
        }

        String otp = String.valueOf(otpObj);
        userService.verifyEmailUpdate(request, otp);
        return ResponseEntity.ok(Map.of("message", "Email updated successfully."));
    }
}
