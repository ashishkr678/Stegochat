package stegochat.stegochat.service;

public interface ForgotPasswordService {

    void sendOtpForPasswordReset(String email);
    
    void verifyOtpForPasswordReset(String otp);

    void resetPassword(String newPassword);

}
