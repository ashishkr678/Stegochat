package stegochat.stegochat.service;

public interface EmailOtpService {
    
    void sendOtp(String email, String type);  // Common method for registration and email update

    void verifyOtp(String email, int otp, String type); // Common verification

    boolean isEmailVerified(String email);

    void clearVerification(String email);

}
