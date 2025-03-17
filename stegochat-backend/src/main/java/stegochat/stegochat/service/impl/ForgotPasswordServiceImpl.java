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

        // Send OTP for Password Reset
        @Override
        public String sendOtpForPasswordReset(String username) {
                UsersEntity user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

                String userEmail = user.getEmail();

                pendingPasswordResetRepository.deleteByEmail(userEmail);

                PendingPasswordResetEntity pendingReset = PendingPasswordResetEntity.builder()
                                .email(userEmail)
                                .createdAt(LocalDateTime.now())
                                .verified(false)
                                .build();

                pendingPasswordResetRepository.save(pendingReset);

                emailOtpService.sendOtp(userEmail, OtpType.PASSWORD_RESET);

                return userEmail;
        }

        // Verify OTP
        @Override
        public void verifyOtpForPasswordReset(String username, String otp) {
                UsersEntity user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

                String email = user.getEmail();

                PendingPasswordResetEntity pendingReset = pendingPasswordResetRepository
                                .findByEmailAndVerifiedFalse(email)
                                .orElseThrow(() -> new BadRequestException("No pending password reset request found."));

                emailOtpService.verifyOtp(email, otp, OtpType.PASSWORD_RESET);

                pendingReset.setVerified(true);
                pendingPasswordResetRepository.save(pendingReset);
        }

        // Reset Password
        @Override
        public void resetPassword(String username, String newPassword) {
                UsersEntity user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

                String email = user.getEmail();

                PendingPasswordResetEntity pendingReset = pendingPasswordResetRepository
                                .findByEmailAndVerifiedTrue(email)
                                .orElseThrow(() -> new BadRequestException("OTP not verified. Cannot reset password."));

                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);

                pendingPasswordResetRepository.deleteByEmail(email);
        }
}
