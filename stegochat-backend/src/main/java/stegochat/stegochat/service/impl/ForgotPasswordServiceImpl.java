// package stegochat.stegochat.service.impl;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Service;

// import jakarta.servlet.http.HttpSession;
// import stegochat.stegochat.entity.UsersEntity;
// import stegochat.stegochat.exception.BadRequestException;
// import stegochat.stegochat.exception.ResourceNotFoundException;
// import stegochat.stegochat.repository.UserRepository;
// import stegochat.stegochat.service.EmailOtpService;
// import stegochat.stegochat.service.ForgotPasswordService;


// @Service
// public class ForgotPasswordServiceImpl implements ForgotPasswordService {

//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private EmailOtpService emailOtpService;

//     @Autowired
//     private BCryptPasswordEncoder passwordEncoder;

//     @Autowired
//     private HttpSession httpSession;

//     @Override
//     public void sendOtpForPasswordReset(String username) {
//         // Find user by username and get email
//         UsersEntity user = userRepository.findByUsername(username)
//                 .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

//         String userEmail = user.getEmail(); // Store email for next steps

//         // Store a flag in session that OTP has been sent
//         httpSession.setAttribute("otpSent", true);
//         httpSession.setAttribute("email", userEmail);

//         // Send OTP using EmailOtpService
//         emailOtpService.sendOtp(userEmail, "FORGOT_PASSWORD");
//     }

//     @Override
//     public void verifyOtpForPasswordReset(int otp) {
//         if (httpSession.getAttribute("otpSent") == null || !(Boolean) httpSession.getAttribute("otpSent")) {
//             throw new BadRequestException("OTP not sent. Please request an OTP first.");
//         }

//         String email = (String) httpSession.getAttribute("email");
//         if (email == null) {
//             throw new BadRequestException("Invalid session. OTP verification failed.");
//         }

//         // Verify OTP using EmailOtpService
//         emailOtpService.verifyOtp(email, otp, "FORGOT_PASSWORD");

//         // Clear OTP and session attributes after successful verification
//         emailOtpService.clearVerification(email);
//         httpSession.removeAttribute("otpSent");
//     }

//     @Override
//     public void resetPassword(String newPassword) {
//         if (httpSession.getAttribute("otpSent") == null || !(Boolean) httpSession.getAttribute("otpSent")) {
//             throw new BadRequestException("OTP not verified. Cannot reset password.");
//         }

//         String email = (String) httpSession.getAttribute("email");
//         if (email == null) {
//             throw new BadRequestException("Invalid session. Password reset failed.");
//         }

//         // Find user by stored email
//         UsersEntity user = userRepository.findByEmail(email)
//                 .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

//         // Encode new password and update user
//         user.setPassword(passwordEncoder.encode(newPassword));
//         userRepository.save(user);

//         // Erase the session variables for security
//         httpSession.removeAttribute("email");
//         httpSession.removeAttribute("otpSent");
//     }
// }
