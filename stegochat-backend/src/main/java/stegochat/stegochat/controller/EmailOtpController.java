package stegochat.stegochat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stegochat.stegochat.service.EmailOtpService;

import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class EmailOtpController {

    private final EmailOtpService emailOtpService;

    public EmailOtpController(EmailOtpService emailOtpService) {
        this.emailOtpService = emailOtpService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String type = requestBody.get("type");

        if (email == null || type == null) {
            throw new IllegalArgumentException("Email and type are required!");
        }

        emailOtpService.sendOtp(email, type);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully to " + email));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody Map<String, Object> requestBody) {
        String email = (String) requestBody.get("email");
        Integer otp = (Integer) requestBody.get("otp");
        String type = (String) requestBody.get("type");

        if (email == null || otp == null || type == null) {
            throw new IllegalArgumentException("Email, OTP, and type are required!");
        }

        emailOtpService.verifyOtp(email, otp, type);
        return ResponseEntity.ok(Map.of("message", "OTP verified successfully."));
    }
}
