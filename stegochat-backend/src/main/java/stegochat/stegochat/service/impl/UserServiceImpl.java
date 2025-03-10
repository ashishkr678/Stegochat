package stegochat.stegochat.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.entity.OtpEntity;
import stegochat.stegochat.entity.PendingRegistrationEntity;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.exception.*;
import stegochat.stegochat.mapper.UserMapper;
import stegochat.stegochat.repository.OtpRepository;
import stegochat.stegochat.repository.PendingRegistrationRepository;
import stegochat.stegochat.repository.UserRepository;
import stegochat.stegochat.security.CookieUtil;
import stegochat.stegochat.security.JwtUtil;
import stegochat.stegochat.service.EmailOtpService;
import stegochat.stegochat.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailOtpService emailOtpService;
    private final PendingRegistrationRepository pendingRegistrationRepository;
    private final OtpRepository otpRepository;

    @Override
    @Transactional
    public void initiateRegistration(UserDTO userDTO) {
        if (userDTO == null || userDTO.getEmail() == null || userDTO.getUsername() == null) {
            throw new BadRequestException("Invalid user data. Email and username are required.");
        }

        // Check if email or username is already registered
        if (userRepository.existsByEmail(userDTO.getEmail())
                || pendingRegistrationRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already registered or pending verification.");
        }
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken.");
        }

        // Hash the password before storing
        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

        // Store user in pending registration
        PendingRegistrationEntity pendingUser = PendingRegistrationEntity.builder()
                .email(userDTO.getEmail())
                .username(userDTO.getUsername())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .phone(userDTO.getPhone())
                .password(hashedPassword) // Storing hashed password
                .profilePicture(userDTO.getProfilePicture())
                .about(userDTO.getAbout())
                .dateOfBirth(userDTO.getDateOfBirth())
                .createdAt(LocalDateTime.now()) // Timestamp
                .build();

        pendingRegistrationRepository.save(pendingUser);

        emailOtpService.sendOtp(userDTO.getEmail(), "REGISTER");

    }

    @Override
    @Transactional
    public UserDTO completeRegistration(String email, String otp) { // ✅ Email is now passed from frontend
        // ✅ Step 1: Find OTP entry using email
        Optional<OtpEntity> otpEntityOpt = otpRepository.findByEmailAndType(email, "REGISTER");

        if (otpEntityOpt.isEmpty()) {
            throw new BadRequestException("No OTP found for this email. Please request OTP again.");
        }

        OtpEntity otpEntity = otpEntityOpt.get();

        // ✅ Step 2: Verify if the OTP is expired
        if (otpEntity.isExpired()) {
            otpRepository.delete(otpEntity);
            throw new BadRequestException("OTP expired. Please request a new one.");
        }

        // ✅ Step 3: Compare user-entered OTP with stored hashed OTP
        if (!passwordEncoder.matches(otp, otpEntity.getOtp())) {
            throw new BadRequestException("Incorrect OTP. Please try again.");
        }

        // ✅ Step 4: Find the pending registration data using email
        Optional<PendingRegistrationEntity> pendingUserOpt = pendingRegistrationRepository.findByEmail(email);

        if (pendingUserOpt.isEmpty()) {
            throw new BadRequestException("No pending registration found. Please register again.");
        }

        PendingRegistrationEntity pendingUser = pendingUserOpt.get();

        // ✅ Step 5: Convert PendingRegistrationEntity to UsersEntity
        UsersEntity newUser = UserMapper.toEntity(pendingUser);
        newUser.setCreatedAt(LocalDateTime.now());

        // ✅ Step 6: Add metadata (Hidden in DTO)
        newUser.setMetadata(Map.of(
                "createdAt", LocalDateTime.now(),
                "accountStatus", "ACTIVE"));

        // ✅ Step 7: Save the new user
        userRepository.save(newUser);

        // ✅ Step 8: Cleanup - Remove OTP & Pending Registration
        pendingRegistrationRepository.deleteByEmail(email);
        otpRepository.deleteByEmailAndType(email, "REGISTER");

        // ✅ Step 9: Return UserDTO (Excluding metadata & password)
        return UserMapper.toDTO(newUser);
    }

    @Override
    public void loginUser(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password.");
        }

        if (user.getMetadata() == null) {
            user.setMetadata(new HashMap<>());
        }

        user.getMetadata().put("lastLogin", LocalDateTime.now());
        user.getMetadata().put("lastLoginIP", request.getRemoteAddr()); // Store IP Address
        user.getMetadata().put("lastLoginDevice", request.getHeader("User-Agent")); // Store Device Info

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());

        Cookie jwtCookie = createCookie("jwt", token, true);
        Cookie usernameCookie = createCookie("username", user.getUsername(), false);

        response.addHeader("Set-Cookie", CookieUtil.createCookieWithSameSite(jwtCookie, getSameSiteValue()));
        response.addHeader("Set-Cookie", CookieUtil.createCookieWithSameSite(usernameCookie, getSameSiteValue()));
    }

    @Override
    public UserDTO getUserProfile(HttpServletRequest request) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return UserMapper.toDTO(user);
    }

    @Override
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::toDTO)
                .or(() -> {
                    throw new ResourceNotFoundException("User with username '" + username + "' not found.");
                });
    }

    @Override
    public void changePassword(HttpServletRequest request, String currentPassword, String newPassword) {
        String username = CookieUtil.extractUsernameFromCookie(request);
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void updatePhoneNumber(HttpServletRequest request, String newPhoneNumber) {
        String username = CookieUtil.extractUsernameFromCookie(request);
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (newPhoneNumber == null || newPhoneNumber.trim().isEmpty()) {
            throw new BadRequestException("Phone number cannot be empty.");
        }

        user.setPhone(newPhoneNumber);
        userRepository.save(user);
    }

    @Override
    public void logout(String username, HttpServletRequest request, HttpServletResponse response) {
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found."));

        if (user.getMetadata() == null) {
            user.setMetadata(new HashMap<>());
        }

        // ✅ Store the last logout time
        user.getMetadata().put("lastLogout", LocalDateTime.now());

        userRepository.save(user); // Save updated metadata

        // ✅ Clear authentication cookies
        Cookie jwtCookie = createCookie("jwt", "", true);
        jwtCookie.setMaxAge(0); // Expire immediately

        Cookie usernameCookie = createCookie("username", "", false);
        usernameCookie.setMaxAge(0); // Expire immediately

        response.addHeader("Set-Cookie", CookieUtil.createCookieWithSameSite(jwtCookie, getSameSiteValue()));
        response.addHeader("Set-Cookie", CookieUtil.createCookieWithSameSite(usernameCookie, getSameSiteValue()));
    }

    private Cookie createCookie(String name, String value, boolean httpOnly) {
        boolean isProduction = "prod".equals(System.getenv("PROFILES_ACTIVE"));

        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setPath("/");
        cookie.setSecure(isProduction);
        cookie.setMaxAge(value == null ? 0 : 10 * 60 * 60);

        return cookie;
    }

    private String getSameSiteValue() {
        return "prod".equals(System.getenv("PROFILES_ACTIVE")) ? "None" : "Lax";
    }
}
