package stegochat.stegochat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stegochat.stegochat.dto.otp.OtpRequestDto;
import stegochat.stegochat.dto.otp.OtpVerificationDto;
import stegochat.stegochat.service.EmailOtpService;

import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmailOtpController {

    private final EmailOtpService emailOtpService;

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@Valid @RequestBody OtpRequestDto request) {
        emailOtpService.sendOtp(request.getEmail(), request.getType());
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully to " + request.getEmail()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@Valid @RequestBody OtpVerificationDto request) {
        emailOtpService.verifyOtp(request.getEmail(), request.getOtp(), request.getType());
        return ResponseEntity.ok(Map.of("message", "OTP verified successfully."));
    }
}
