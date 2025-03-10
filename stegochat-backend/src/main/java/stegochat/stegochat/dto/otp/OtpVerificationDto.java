package stegochat.stegochat.dto.otp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OtpVerificationDto { 
    @Email(message = "Invalid email format")
    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Type is required")
    private String type;

    @NotNull(message = "OTP is required for verification")
    private String otp;
}
