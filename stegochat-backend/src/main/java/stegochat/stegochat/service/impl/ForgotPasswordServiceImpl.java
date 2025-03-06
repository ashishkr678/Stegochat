package stegochat.stegochat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.exception.ResourceNotFoundException;
import stegochat.stegochat.repository.UserRepository;
import stegochat.stegochat.service.EmailOtpService;
import stegochat.stegochat.service.ForgotPasswordService;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailOtpService emailOtpService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Temporary variable to store user email during the reset process
    private String tempEmail;

    @Override
    public void sendOtpForPasswordReset(String username) {
        // Find user by username and get email
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        this.tempEmail = user.getEmail(); // Store email for next steps

        // Send OTP using EmailOtpService
        emailOtpService.sendOtp(tempEmail, "FORGOT_PASSWORD");
    }

    @Override
    public void verifyOtpForPasswordReset(int otp) {
        if (tempEmail == null) {
            throw new BadRequestException("OTP verification request is invalid or expired!");
        }

        // Verify OTP using EmailOtpService
        emailOtpService.verifyOtp(tempEmail, otp, "FORGOT_PASSWORD");
    }

    @Override
    public void resetPassword(String newPassword) {
        if (tempEmail == null) {
            throw new BadRequestException("Reset request is invalid or expired!");
        }

        // Find user by stored email
        UsersEntity user = userRepository.findByEmail(tempEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        // Encode new password and update user
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Erase the temporary variable for security
        tempEmail = null;
    }
}
