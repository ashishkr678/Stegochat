package stegochat.stegochat.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.entity.OtpEntity;
import stegochat.stegochat.entity.PendingRegistrationEntity;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.entity.enums.OtpType;
import stegochat.stegochat.exception.*;
import stegochat.stegochat.mapper.UserMapper;
import stegochat.stegochat.repository.OtpRepository;
import stegochat.stegochat.repository.PendingRegistrationRepository;
import stegochat.stegochat.repository.UserRepository;
import stegochat.stegochat.security.CookieUtil;
import stegochat.stegochat.security.EncryptionUtil;
import stegochat.stegochat.security.JwtUtil;
import stegochat.stegochat.service.EmailOtpService;
import stegochat.stegochat.service.UserService;
import jakarta.servlet.http.*;

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

    // ✅ Initiate Registration with OTP
    @Override
    @Transactional
    public void initiateRegistration(UserDTO userDTO) {
        if (userDTO == null || userDTO.getEmail() == null || userDTO.getUsername() == null) {
            throw new BadRequestException("Invalid user data. Email and username are required.");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())
                || pendingRegistrationRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email is already registered or pending verification.");
        }
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new DuplicateResourceException("Username is already taken.");
        }

        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

        PendingRegistrationEntity pendingUser = PendingRegistrationEntity.builder()
                .email(userDTO.getEmail())
                .username(userDTO.getUsername())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .phone(userDTO.getPhone())
                .password(hashedPassword)
                .profilePicture(userDTO.getProfilePicture())
                .about(userDTO.getAbout())
                .dateOfBirth(userDTO.getDateOfBirth())
                .createdAt(LocalDateTime.now())
                .build();

        pendingRegistrationRepository.save(pendingUser);

        emailOtpService.sendOtp(userDTO.getEmail(), OtpType.REGISTRATION);
    }

    // ✅ Complete Registration with OTP Verification
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
        newUser.setMetadata(Map.of(
                "createdAt", LocalDateTime.now(),
                "accountStatus", "ACTIVE"));

        userRepository.save(newUser);
        pendingRegistrationRepository.deleteByEmail(email);
        otpRepository.deleteByEmailAndType(email, OtpType.REGISTRATION);

        return UserMapper.toDTO(newUser);
    }

    // ✅ Initiate Email Update with OTP
    @Override
    public void initiateEmailUpdate(HttpServletRequest request, String newEmail) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        if (userRepository.existsByEmail(newEmail)) {
            throw new BadRequestException("This email is already registered.");
        }

        userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found."));

        emailOtpService.sendOtp(newEmail, OtpType.EMAIL_UPDATE);
    }

    // ✅ Verify Email Update with OTP
    @Override
    public void verifyEmailUpdate(HttpServletRequest request, String otp) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found."));

        emailOtpService.verifyOtp(user.getEmail(), otp, OtpType.EMAIL_UPDATE);

        userRepository.save(user);
        otpRepository.deleteByEmailAndType(user.getEmail(), OtpType.EMAIL_UPDATE);
    }

    // ✅ Resend OTP Dynamically
    @Override
    public void resendOtp(String email, OtpType otpType) {
        emailOtpService.resendOtp(email, otpType);
    }

    // ✅ User Login
    @Override
    public UserDTO loginUser(String username, String password, HttpServletRequest request,
            HttpServletResponse response) {
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid username or password.");
        }

        if (user.getMetadata() == null) {
            user.setMetadata(new HashMap<>());
        }
        user.getMetadata().put("lastLogin", LocalDateTime.now());
        user.getMetadata().put("lastLoginIP", request.getRemoteAddr());
        user.getMetadata().put("lastLoginDevice", request.getHeader("User-Agent"));

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());

        Cookie jwtCookie = CookieUtil.createCookie("jwt", token, true);
        Cookie usernameCookie = CookieUtil.createCookie("username", user.getUsername(), false);

        response.addHeader("Set-Cookie", CookieUtil.createCookieWithSameSite(jwtCookie, CookieUtil.getSameSiteValue()));
        response.addHeader("Set-Cookie",
                CookieUtil.createCookieWithSameSite(usernameCookie, CookieUtil.getSameSiteValue()));

        UserDTO userDTO = UserMapper.toDTO(user);

        HttpSession session = request.getSession();
        session.setAttribute("userProfile", userDTO);

        return userDTO;
    }

    @Override
    public UserDTO getUserProfile(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("userProfile") != null) {
            return (UserDTO) session.getAttribute("userProfile");
        }

        String username = CookieUtil.extractUsernameFromCookie(request);
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        UserDTO userDTO = UserMapper.toDTO(user);

        if (session != null) {
            session.setAttribute("userProfile", userDTO);
        }

        return userDTO;
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

    // ✅ User Logout
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        if (username == null) {
            throw new AuthenticationException("User not logged in.");
        }

        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User not found."));

        if (user.getMetadata() == null) {
            user.setMetadata(new HashMap<>());
        }
        user.getMetadata().put("lastLogout", LocalDateTime.now());
        userRepository.save(user);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie jwtCookie = CookieUtil.createCookie("jwt", "", true);
        jwtCookie.setMaxAge(0);

        Cookie usernameCookie = CookieUtil.createCookie("username", "", false);
        usernameCookie.setMaxAge(0);

        response.addHeader("Set-Cookie", CookieUtil.createCookieWithSameSite(jwtCookie, "Strict"));
        response.addHeader("Set-Cookie", CookieUtil.createCookieWithSameSite(usernameCookie, "Strict"));
    }
}
