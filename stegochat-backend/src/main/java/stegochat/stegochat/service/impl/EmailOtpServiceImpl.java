package stegochat.stegochat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import stegochat.stegochat.dto.OTPData;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.repository.UserRepository;
import stegochat.stegochat.service.EmailOtpService;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailOtpServiceImpl implements EmailOtpService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    private final Random random = new SecureRandom();
    private final Map<String, OTPData> otpStorage = new ConcurrentHashMap<>();

    @Override
    public void sendOtp(String email, String type) {
        if ("UPDATE_EMAIL".equals(type) && userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException("Email already in use!");
        }

        if (otpStorage.containsKey(email) && !otpStorage.get(email).isExpired()) {
            throw new BadRequestException("OTP already sent! Try again later.");
        }

        int otp = 100000 + random.nextInt(900000);
        OTPData otpData = new OTPData(otp, email, type);
        otpStorage.put(email, otpData);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp + "\nOTP will expire in 15 minutes.");
        mailSender.send(message);
    }

    @Override
    public void verifyOtp(String email, int otp, String type) {
        OTPData otpData = otpStorage.get(email);

        if (otpData == null) {
            throw new BadRequestException("No OTP request found. Please request OTP first.");
        }

        if (otpData.isExpired()) {
            otpStorage.remove(email);
            throw new BadRequestException("OTP expired. Request a new one.");
        }

        if (otpData.getOtp() != otp) {
            throw new BadRequestException("Incorrect OTP. Please try again.");
        }

        otpData.markVerified(); // Mark OTP as verified
    }

    public boolean isEmailVerified(String email) {
        return otpStorage.containsKey(email) && otpStorage.get(email).isVerified();
    }

    public void clearVerification(String email) {
        otpStorage.remove(email);
    }
}
