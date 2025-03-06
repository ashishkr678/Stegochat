package stegochat.stegochat.dto;

import java.time.LocalDateTime;

public class OTPData {
    private final int otp;
    private final String email;
    private final String type; // "REGISTRATION", "UPDATE_EMAIL", "FORGOT_PASSWORD"
    private final LocalDateTime createdAt;
    private boolean verified;

    public OTPData(int otp, String email, String type) {
        this.otp = otp;
        this.email = email;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.verified = false;
    }

    public int getOtp() {
        return otp;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isExpired() {
        return createdAt.plusMinutes(15).isBefore(LocalDateTime.now());
    }

    public boolean isVerified() {
        return verified;
    }

    public void markVerified() {
        this.verified = true;
    }
}
