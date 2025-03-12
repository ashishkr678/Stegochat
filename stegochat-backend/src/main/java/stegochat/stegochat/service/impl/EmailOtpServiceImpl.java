package stegochat.stegochat.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import stegochat.stegochat.entity.OtpEntity;
import stegochat.stegochat.entity.OtpAttemptLog;
import stegochat.stegochat.entity.enums.OtpType;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.repository.OtpRepository;
import stegochat.stegochat.repository.OtpAttemptLogRepository;
import stegochat.stegochat.service.EmailOtpService;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailOtpServiceImpl implements EmailOtpService {

    private final JavaMailSender mailSender;
    private final OtpRepository otpRepository;
    private final OtpAttemptLogRepository otpAttemptLogRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int OTP_RESEND_LIMIT = 3;
    private static final int OTP_GLOBAL_LIMIT = 5; // Across all types

    @Override
    public synchronized void sendOtp(String email, OtpType type) {
        enforceRateLimits(email, type);

        String otp = String.valueOf(100000 + RANDOM.nextInt(900000));
        String hashedOtp = passwordEncoder.encode(otp);

        otpRepository.deleteByEmailAndType(email, type);

        OtpEntity otpData = OtpEntity.builder()
                .email(email)
                .otp(hashedOtp)
                .type(type)
                .createdAt(LocalDateTime.now())
                .verified(false)
                .failedAttempts(0)
                .build();

        otpRepository.save(otpData);
        logOtpAttempt(email, type, true, "OTP sent");

        sendOtpEmail(email, otp);
    }

    @Override
    public synchronized void verifyOtp(String email, String otp, OtpType type) {
        Optional<OtpEntity> otpDataOpt = otpRepository.findByEmailAndType(email, type);

        if (otpDataOpt.isEmpty()) {
            logOtpAttempt(email, type, false, "No OTP request found");
            throw new BadRequestException("No OTP request found. Please request OTP first.");
        }

        OtpEntity otpData = otpDataOpt.get();

        if (otpData.isExpired()) {
            otpRepository.delete(otpData);
            logOtpAttempt(email, type, false, "OTP expired");
            throw new BadRequestException("OTP expired. Please request a new one.");
        }

        if (!passwordEncoder.matches(otp, otpData.getOtp())) {
            otpData.setFailedAttempts(otpData.getFailedAttempts() + 1);
            otpRepository.save(otpData);
            logOtpAttempt(email, type, false, "Incorrect OTP");

            if (otpData.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                throw new BadRequestException("Too many failed attempts. OTP locked for this type.");
            }

            throw new BadRequestException("Incorrect OTP. Please try again.");
        }

        otpData.setVerified(true);
        otpRepository.save(otpData);
        logOtpAttempt(email, type, true, "OTP verified successfully");
    }

    @Override
    public synchronized void resendOtp(String email, OtpType type) {
        enforceResendLimit(email, type);
        sendOtp(email, type);
    }

    @Override
    public void clearVerification(String email, OtpType type) {
        otpRepository.findByEmailAndType(email, type).ifPresent(otpRepository::delete);
    }

    private void enforceRateLimits(String email, OtpType type) {
        long count = otpRepository.findByEmail(email).size();
        if (count >= OTP_GLOBAL_LIMIT) {
            throw new BadRequestException("You have reached the global OTP request limit.");
        }
    }

    private void enforceResendLimit(String email, OtpType type) {
        long count = otpRepository.findByEmailAndType(email, type).stream().count();
        if (count >= OTP_RESEND_LIMIT) {
            throw new BadRequestException("You have reached the OTP resend limit.");
        }
    }

    private void logOtpAttempt(String email, OtpType type, boolean success, String reason) {
        OtpAttemptLog log = OtpAttemptLog.builder()
                .email(email)
                .type(type)
                .success(success)
                .reason(reason)
                .timestamp(LocalDateTime.now())
                .build();
        otpAttemptLogRepository.save(log);
    }

    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp + "\n\nIt will expire in 15 minutes.");
        mailSender.send(message);
    }
}
