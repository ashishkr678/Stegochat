package stegochat.stegochat.service;

public interface ForgotPasswordService {

    String sendOtpForPasswordReset(String email);
    
    void verifyOtpForPasswordReset(String username, String otp);

    void resetPassword(String username, String newPassword);

}
