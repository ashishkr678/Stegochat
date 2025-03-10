// package stegochat.stegochat.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import stegochat.stegochat.exception.BadRequestException;
// import stegochat.stegochat.exception.ResourceNotFoundException;
// import stegochat.stegochat.service.ForgotPasswordService;

// import java.util.Map;

// @RestController
// @CrossOrigin("*")
// @RequestMapping("/api/forgot-password")
// public class ForgotPasswordController {

//     @Autowired
//     private ForgotPasswordService forgotPasswordService;

//     // 1️⃣ ✅ User provides username → Sends OTP
//     @PostMapping("/send-otp")
//     public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
//         String username = request.get("username");

//         if (username == null || username.trim().isEmpty()) {
//             return ResponseEntity.badRequest().body(Map.of("error", "Username is required!"));
//         }

//         try {
//             forgotPasswordService.sendOtpForPasswordReset(username);
//             return ResponseEntity.ok(Map.of("message", "OTP sent successfully!"));
//         } catch (ResourceNotFoundException e) {
//             return ResponseEntity.status(404).body(Map.of("error", "User Not Found", "message", e.getMessage()));
//         }
//     }

//     // 2️⃣ ✅ User provides only OTP → Verifies identity
//     @PostMapping("/verify-otp")
//     public ResponseEntity<?> verifyOtp(@RequestBody Map<String, Object> request) {
//         Integer otp = (Integer) request.get("otp");

//         if (otp == null) {
//             return ResponseEntity.badRequest().body(Map.of("error", "OTP is required!"));
//         }

//         try {
//             forgotPasswordService.verifyOtpForPasswordReset(otp);
//             return ResponseEntity.ok(Map.of("message", "OTP verified successfully!"));
//         } catch (BadRequestException e) {
//             return ResponseEntity.status(400).body(Map.of("error", "Bad Request", "message", e.getMessage()));
//         }
//     }

//     // 3️⃣ ✅ User provides only newPassword → Resets password
//     @PostMapping("/reset-password")
//     public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
//         String newPassword = request.get("newPassword");

//         if (newPassword == null || newPassword.trim().isEmpty()) {
//             return ResponseEntity.badRequest().body(Map.of("error", "New password is required!"));
//         }

//         try {
//             forgotPasswordService.resetPassword(newPassword);
//             return ResponseEntity.ok(Map.of("message", "Password reset successfully!"));
//         } catch (BadRequestException | ResourceNotFoundException e) {
//             return ResponseEntity.status(400).body(Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage()));
//         }
//     }
// }
