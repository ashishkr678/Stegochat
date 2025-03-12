package stegochat.stegochat.service;

public interface ForgotPasswordService {

    void sendOtpForPasswordReset(String email);
    
    void verifyOtpForPasswordReset(String username, String otp);

    void resetPassword(String username, String newPassword);

}
