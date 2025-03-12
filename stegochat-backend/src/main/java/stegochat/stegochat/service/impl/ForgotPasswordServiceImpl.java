package stegochat.stegochat.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import stegochat.stegochat.entity.PendingPasswordResetEntity;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.entity.enums.OtpType;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.exception.ResourceNotFoundException;
import stegochat.stegochat.repository.PendingPasswordResetRepository;
import stegochat.stegochat.repository.UserRepository;
import stegochat.stegochat.service.EmailOtpService;
import stegochat.stegochat.service.ForgotPasswordService;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final UserRepository userRepository;
    private final EmailOtpService emailOtpService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PendingPasswordResetRepository pendingPasswordResetRepository;

    @Override
    public void sendOtpForPasswordReset(String username) {
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        String userEmail = user.getEmail();

        // ✅ Remove any previous reset requests
        pendingPasswordResetRepository.deleteByEmail(userEmail);

        // ✅ Save new reset request
        PendingPasswordResetEntity pendingReset = PendingPasswordResetEntity.builder()
                .email(userEmail)
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        pendingPasswordResetRepository.save(pendingReset);

        emailOtpService.sendOtp(userEmail, OtpType.PASSWORD_RESET);
    }

    @Override
    public void verifyOtpForPasswordReset(String otp) {
        String otpString = String.valueOf(otp);

        // ✅ Optimized lookup using MongoDB query
        PendingPasswordResetEntity pendingReset = pendingPasswordResetRepository.findByEmailAndVerifiedFalse(otp)
                .orElseThrow(() -> new BadRequestException("No pending password reset request found."));

        String email = pendingReset.getEmail();

        emailOtpService.verifyOtp(email, otpString, OtpType.PASSWORD_RESET);

        // ✅ Mark request as verified
        pendingReset.setVerified(true);
        pendingPasswordResetRepository.save(pendingReset);
    }

    @Override
    public void resetPassword(String newPassword) {
        // ✅ Optimized lookup for verified request
        PendingPasswordResetEntity pendingReset = pendingPasswordResetRepository.findByEmailAndVerifiedTrue(newPassword)
                .orElseThrow(() -> new BadRequestException("OTP not verified. Cannot reset password."));

        String email = pendingReset.getEmail();

        UsersEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        // ✅ Update user password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // ✅ Cleanup after successful reset
        pendingPasswordResetRepository.deleteByEmail(email);
    }
}
