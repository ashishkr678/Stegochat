package stegochat.stegochat.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import stegochat.stegochat.entity.PendingPasswordResetEntity;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.exception.ResourceNotFoundException;
import stegochat.stegochat.repository.PendingPasswordResetRepository;
import stegochat.stegochat.repository.UserRepository;
import stegochat.stegochat.service.EmailOtpService;
import stegochat.stegochat.service.ForgotPasswordService;

import java.util.Optional;
import java.util.Date;

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

        PendingPasswordResetEntity pendingReset = PendingPasswordResetEntity.builder()
                .email(userEmail)
                .createdAt(new Date())
                .verified(false)
                .build();

        pendingPasswordResetRepository.save(pendingReset);

        emailOtpService.sendOtp(userEmail, "FORGOT_PASSWORD");
    }

    @Override
    public void verifyOtpForPasswordReset(int otp) {

        String otpString = String.valueOf(otp);

        Optional<PendingPasswordResetEntity> pendingResetOpt = pendingPasswordResetRepository.findAll()
                .stream()
                .filter(pending -> !pending.isVerified())
                .findFirst();

        if (pendingResetOpt.isEmpty()) {
            throw new BadRequestException("No pending password reset request found.");
        }

        PendingPasswordResetEntity pendingReset = pendingResetOpt.get();
        String email = pendingReset.getEmail();

        emailOtpService.verifyOtp(email, otpString, "FORGOT_PASSWORD");

        pendingReset.setVerified(true);
        pendingPasswordResetRepository.save(pendingReset);
    }

    @Override
    public void resetPassword(String newPassword) {
        Optional<PendingPasswordResetEntity> pendingResetOpt = pendingPasswordResetRepository.findAll()
                .stream()
                .filter(PendingPasswordResetEntity::isVerified)
                .findFirst();

        if (pendingResetOpt.isEmpty()) {
            throw new BadRequestException("OTP not verified. Cannot reset password.");
        }

        PendingPasswordResetEntity pendingReset = pendingResetOpt.get();
        String email = pendingReset.getEmail();

        UsersEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        pendingPasswordResetRepository.deleteByEmail(email);
    }
}
