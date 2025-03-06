package stegochat.stegochat.service;

public interface ForgotPasswordService {

    void sendOtpForPasswordReset(String email);
    
    void verifyOtpForPasswordReset(int otp);

    void resetPassword(String newPassword);

}