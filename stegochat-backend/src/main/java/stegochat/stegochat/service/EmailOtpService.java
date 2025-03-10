package stegochat.stegochat.service;

public interface EmailOtpService {
    
    void sendOtp(String email, String type);

    void verifyOtp(String email, String otp, String type);

    void clearVerification(String email, String type);

}
