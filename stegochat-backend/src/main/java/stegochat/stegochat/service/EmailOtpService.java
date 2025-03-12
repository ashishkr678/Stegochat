package stegochat.stegochat.service;

import stegochat.stegochat.entity.enums.OtpType;

public interface EmailOtpService {

    void sendOtp(String email, OtpType type);

    void verifyOtp(String email, String otp, OtpType type);

    void resendOtp(String email, OtpType type);

    void clearVerification(String email, OtpType type);
}
