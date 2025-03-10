package stegochat.stegochat.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import stegochat.stegochat.entity.OtpEntity;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.repository.OtpRepository;
import stegochat.stegochat.service.EmailOtpService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailOtpServiceImpl implements EmailOtpService {

    private final JavaMailSender mailSender;
    private final OtpRepository otpRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public synchronized void sendOtp(String email, String type) {
        // Generate a 6-digit OTP (ensuring it doesn’t start with zero)
        String otp = String.valueOf(100000 + RANDOM.nextInt(900000));

        // ✅ Hash OTP before storing it in the database
        String hashedOtp = passwordEncoder.encode(otp);

        OtpEntity otpData = OtpEntity.builder()
                .email(email)
                .otp(hashedOtp) // ✅ Store hashed OTP
                .type(type)
                .createdAt(LocalDateTime.now())
                .verified(false)
                .build();

        // Save OTP to the database
        otpRepository.save(otpData);

        // Send OTP via email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp + "\n\nIt will expire in 15 minutes.");
        mailSender.send(message);
    }

    @Override
    public synchronized void verifyOtp(String email, String otp, String type) {
        Optional<OtpEntity> otpDataOpt = otpRepository.findByEmailAndType(email, type);

        if (otpDataOpt.isEmpty()) {
            throw new BadRequestException("No OTP request found. Please request OTP first.");
        }

        OtpEntity otpData = otpDataOpt.get();

        // ✅ Check if the OTP is expired
        if (otpData.isExpired()) {
            otpRepository.delete(otpData);
            throw new BadRequestException("OTP expired. Please request a new one.");
        }

        // ✅ Verify hashed OTP using `BCryptPasswordEncoder.matches()`
        if (!passwordEncoder.matches(otp, otpData.getOtp())) {
            throw new BadRequestException("Incorrect OTP. Please try again.");
        }

        // ✅ Mark OTP as verified
        otpData.setVerified(true);
        otpRepository.save(otpData);
    }

    @Override
    public void clearVerification(String email, String type) {
        otpRepository.findByEmailAndType(email, type)
                .ifPresent(otpRepository::delete);
    }
}
