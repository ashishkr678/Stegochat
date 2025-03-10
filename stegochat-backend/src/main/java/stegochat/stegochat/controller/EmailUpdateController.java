// package stegochat.stegochat.controller;

// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import jakarta.servlet.http.HttpSession;
// import stegochat.stegochat.entity.UsersEntity;
// import stegochat.stegochat.exception.BadRequestException;
// import stegochat.stegochat.repository.UserRepository;
// import stegochat.stegochat.security.CookieUtil;
// import stegochat.stegochat.service.EmailOtpService;
// import stegochat.stegochat.service.UserService;

// @RestController
// @RequestMapping("/api/update-email")
// public class EmailUpdateController {

//     @Autowired
//     private UserService userService;

//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private EmailOtpService emailOtpService;

//     // Step 1: Send OTP to new email
//     @PostMapping("/send-otp")
//     public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> request, HttpSession session) {
//         String newEmail = request.get("email");

//         if (newEmail == null || newEmail.trim().isEmpty()) {
//             return ResponseEntity.badRequest().body(Map.of("error", "Email is required!"));
//         }

//         try {
//             // Check if OTP has already been requested for this email (if already exists in session or cache)
//             if (session.getAttribute("otpEmail") != null && session.getAttribute("otpEmail").equals(newEmail)) {
//                 return ResponseEntity.badRequest()
//                         .body(Map.of("error", "OTP already sent. Please wait before requesting again."));
//             }

//             // Send OTP to the new email
//             emailOtpService.sendOtp(newEmail, "UPDATE_EMAIL");
            
//             // Store the email in the session for subsequent verification
//             session.setAttribute("otpEmail", newEmail);

//             return ResponseEntity.ok(Map.of("message", "OTP sent successfully to " + newEmail));
//         } catch (BadRequestException e) {
//             return ResponseEntity.badRequest().body(Map.of("error", "Bad Request", "message", e.getMessage()));
//         }
//     }

//     // Step 2: Verify OTP (Only OTP is needed, not email)
//     @PostMapping("/verify-otp")
//     public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody Map<String, Integer> request, HttpSession session) {
//         Integer otp = request.get("otp");

//         if (otp == null) {
//             return ResponseEntity.badRequest().body(Map.of("error", "OTP is required!"));
//         }

//         try {
//             // Extract username from session (using a JWT token or cookies) and get the current email
//             String username = CookieUtil.extractUsernameFromCookie(null); // Replace with actual logic to get username from session or JWT

//             UsersEntity user = userRepository.findByUsername(username)
//                     .orElseThrow(() -> new RuntimeException("User not found!"));
//             String email = user.getEmail();

//             if (email == null || email.trim().isEmpty()) {
//                 return ResponseEntity.badRequest().body(Map.of("error", "No email found for user."));
//             }

//             // Get OTP Email from the session
//             String otpEmail = (String) session.getAttribute("otpEmail");

//             if (otpEmail == null || !otpEmail.equals(email)) {
//                 return ResponseEntity.badRequest().body(Map.of("error", "OTP was not requested for this email."));
//             }

//             // Verify the OTP associated with the email
//             emailOtpService.verifyOtp(email, otp, "UPDATE_EMAIL");
//             emailOtpService.clearVerification(email); // Clear OTP from memory after successful verification

//             // Update the user's email after OTP is verified
//             userService.updateEmail(email);
            
//             // Clear the OTP session data after successful verification
//             session.removeAttribute("otpEmail");

//             return ResponseEntity.ok(Map.of("message", "Email updated successfully."));
//         } catch (BadRequestException e) {
//             return ResponseEntity.badRequest().body(Map.of("error", "Bad Request", "message", e.getMessage()));
//         }
//     }
// }
