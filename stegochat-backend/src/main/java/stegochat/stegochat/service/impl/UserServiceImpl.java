package stegochat.stegochat.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.dto.UserSummaryDTO;
import stegochat.stegochat.entity.PendingRegistrationEntity;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.entity.enums.OtpType;
import stegochat.stegochat.exception.AuthenticationException;
import stegochat.stegochat.exception.BadRequestException;
import stegochat.stegochat.exception.DuplicateResourceException;
import stegochat.stegochat.exception.ResourceNotFoundException;
import stegochat.stegochat.mapper.UserMapper;
import stegochat.stegochat.repository.OtpRepository;
import stegochat.stegochat.repository.PendingRegistrationRepository;
import stegochat.stegochat.repository.UserRepository;
import stegochat.stegochat.security.CookieUtil;
import stegochat.stegochat.security.EncryptionUtil;
import stegochat.stegochat.security.JwtUtil;
import stegochat.stegochat.security.WebSocketSessionManager;
import stegochat.stegochat.service.EmailOtpService;
import stegochat.stegochat.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailOtpService emailOtpService;
    private final OtpRepository otpRepository;
    private final PendingRegistrationRepository pendingRegistrationRepository;

    // Initiate Registration with OTP
    @Override
    @Transactional
    public void initiateRegistration(UserDTO userDTO) {
        if (userDTO == null || userDTO.getEmail() == null || userDTO.getUsername() == null) {
            throw new BadRequestException("Invalid user data. Email and username are required.");
        }

        Boolean existsUsername = userRepository.existsByUsername(userDTO.getUsername());
        if (Boolean.TRUE.equals(existsUsername)) {
            throw new DuplicateResourceException("Username already taken.");
        }

        Boolean existsEmail = userRepository.existsByEmail(userDTO.getEmail());
        if (Boolean.TRUE.equals(existsEmail)) {
            throw new DuplicateResourceException("Email already in used.");
        }

        PendingRegistrationEntity pendingUser = UserMapper.toPendingEntity(userDTO, passwordEncoder);
        pendingRegistrationRepository.save(pendingUser);

        emailOtpService.sendOtp(userDTO.getEmail(), OtpType.REGISTRATION);
    }

    // Complete Registration with OTP Verification
    @Override
    @Transactional
    public UserDTO completeRegistration(String email, String otp) {
        emailOtpService.verifyOtp(email, otp, OtpType.REGISTRATION);

        PendingRegistrationEntity pendingUser = pendingRegistrationRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No pending registration found. Please register again."));

        UsersEntity newUser = UserMapper.toEntity(pendingUser);
        newUser.setCreatedAt(LocalDateTime.now());

        String baseEncryptionKey = EncryptionUtil.generateBaseEncryptionKey();
        newUser.getEncryptionKeys().put("baseKey", baseEncryptionKey);
        newUser.setMetadata(Map.of("createdAt", LocalDateTime.now(), "accountStatus", "ACTIVE"));

        userRepository.save(newUser);
        pendingRegistrationRepository.deleteByEmail(email);
        otpRepository.deleteByEmailAndType(email, OtpType.REGISTRATION);

        return UserMapper.toDTO(newUser);
    }

    // User Login & Session Management
    @Override
    public UserDTO loginUser(String username, String password, HttpServletRequest request,
            HttpServletResponse response) {
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        user.getMetadata().put("lastLogin", LocalDateTime.now());
        user.getMetadata().put("lastLoginIP", request.getRemoteAddr());
        user.getMetadata().put("lastLoginDevice", request.getHeader("User-Agent"));

        user.setOnline(true);
        user.setLastSeen(LocalDateTime.now());

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());

        Cookie jwtCookie = CookieUtil.createCookie("jwt", token, true);
        Cookie usernameCookie = CookieUtil.createCookie("username", user.getUsername(), false);

        response.addHeader("Set-Cookie", CookieUtil.createCookieWithSameSite(jwtCookie, "Strict"));
        response.addHeader("Set-Cookie",
                CookieUtil.createCookieWithSameSite(usernameCookie, "Strict"));

        request.getSession().invalidate();
        request.getSession(true).setAttribute("userProfile", UserMapper.toDTO(user));

        return UserMapper.toDTO(user);
    }

    // Initiate Email Update with OTP
    @Override
    public void initiateEmailUpdate(HttpServletRequest request, String newEmail) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        if (userRepository.existsByEmail(newEmail)) {
            throw new BadRequestException("This email is already registered.");
        }

        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found."));

        user.getMetadata().put("pendingEmail", newEmail);
        userRepository.save(user);

        emailOtpService.sendOtp(newEmail, OtpType.EMAIL_UPDATE);
    }

    // Verify Email Update with OTP
    @Override
    public void verifyEmailUpdate(HttpServletRequest request, String otp) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found."));

        String newEmail = (String) user.getMetadata().get("pendingEmail");
        if (newEmail == null) {
            throw new BadRequestException("No email update request found.");
        }

        emailOtpService.verifyOtp(newEmail, otp, OtpType.EMAIL_UPDATE);

        user.setEmail(newEmail);
        user.getMetadata().remove("pendingEmail");
        userRepository.save(user);

        otpRepository.deleteByEmailAndType(newEmail, OtpType.EMAIL_UPDATE);

        UserDTO updatedUserDTO = UserMapper.toDTO(user);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("userProfile", updatedUserDTO);
        }

    }

    // Resend OTP Dynamically
    @Override
    public void resendOtp(String email, OtpType otpType) {
        emailOtpService.resendOtp(email, otpType);
    }

    // Retrieve User Profile
    @Override
    public UserDTO getUserProfile(HttpServletRequest request) {

        String username = CookieUtil.extractUsernameFromCookie(request);
        
        if (username == null || username.isEmpty()) {
            throw new AuthenticationException("Invalid session. Please log in again.");
        }
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return UserMapper.toDTO(user);
    }

    // Search users
    @Override
    public List<UserSummaryDTO> searchUsersByUsername(String query) {
        List<UserSummaryDTO> users = userRepository.findByUsernameStartingWith(query)
                .stream()
                .map(UserMapper::toSummaryDTO)
                .collect(Collectors.toList());

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found.");
        }

        return users;
    }

    // Fetch User by Username
    @Override
    public Optional<UserSummaryDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::toSummaryDTO)
                .or(() -> {
                    throw new ResourceNotFoundException("User not found.");
                });
    }

    // Change Password
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

    // Update Phone Number
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

        UserDTO updatedUserDTO = UserMapper.toDTO(user);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("userProfile", updatedUserDTO);
        }

    }

    // User Logout
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String username = CookieUtil.extractUsernameFromCookie(request);
        if (username == null) {
            throw new AuthenticationException("User not logged in.");
        }

        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User not found."));

        user.getMetadata().put("lastLogout", LocalDateTime.now());
        user.setOnline(false);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);

        WebSocketSessionManager.removeUser(username);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie jwtCookie = CookieUtil.createCookie("jwt", "", true);
        jwtCookie.setMaxAge(0);
        Cookie usernameCookie = CookieUtil.createCookie("username", "", false);
        usernameCookie.setMaxAge(0);

        Cookie jsessionCookie = new Cookie("JSESSIONID", "");
        jsessionCookie.setHttpOnly(true);
        jsessionCookie.setSecure(true);
        jsessionCookie.setPath("/");
        jsessionCookie.setMaxAge(0);

        response.addHeader("Set-Cookie", CookieUtil.createCookieWithSameSite(jwtCookie, "Strict"));
        response.addHeader("Set-Cookie", CookieUtil.createCookieWithSameSite(usernameCookie, "Strict"));
        response.addHeader("Set-Cookie", CookieUtil.createCookieWithSameSite(jsessionCookie, "Strict")); // ✅ Add this
    }

}
