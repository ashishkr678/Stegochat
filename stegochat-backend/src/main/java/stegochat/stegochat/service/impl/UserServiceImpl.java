package stegochat.stegochat.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import stegochat.stegochat.dto.UserDTO;
import stegochat.stegochat.entity.UsersEntity;
import stegochat.stegochat.exception.*;
import stegochat.stegochat.mapper.UserMapper;
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
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailOtpService emailOtpService;
    private final Map<String, UserDTO> pendingRegistrations = new ConcurrentHashMap<>(); // Store pending users
    private final Map<String, String> pendingEmails = new ConcurrentHashMap<>(); // Store email linked to username

    @Override
    public void initiateRegistration(HttpServletRequest request, UserDTO userDTO) {
        if (userDTO == null || userDTO.getEmail() == null || userDTO.getUsername() == null) {
            throw new BadRequestException("Invalid user data. Email and username are required.");
        }

        // Check if username or email is already in use
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken.");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered.");
        }

        // Store pending user data
        pendingRegistrations.put(userDTO.getEmail(), userDTO);
        pendingEmails.put(userDTO.getUsername(), userDTO.getEmail()); // Store username -> email mapping

        // Store email in session for retrieval during completion
        request.getSession().setAttribute("pendingEmail", userDTO.getEmail());

        // Send OTP for registration
        emailOtpService.sendOtp(userDTO.getEmail(), "REGISTRATION");
    }

    @Override
    public UsersEntity completeRegistration(HttpServletRequest request, int otp) {
        // Retrieve email from session
        String email = (String) request.getSession().getAttribute("pendingEmail");

        if (email == null) {
            throw new BadRequestException("No registration session found. Please register again.");
        }

        // Retrieve pending user data
        UserDTO pendingUser = pendingRegistrations.get(email);
        if (pendingUser == null) {
            request.getSession().removeAttribute("pendingEmail"); // Cleanup invalid state
            throw new BadRequestException("Registration session expired. Please register again.");
        }

        // Verify OTP explicitly
        emailOtpService.verifyOtp(email, otp, "REGISTRATION");

        // Convert DTO to Entity
        UsersEntity newUser = UserMapper.toEntity(pendingUser);
        newUser.setPassword(passwordEncoder.encode(pendingUser.getPassword()));

        // Set metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("createdAt", LocalDateTime.now());
        metadata.put("accountStatus", "ACTIVE");
        newUser.setMetadata(metadata);

        // Cleanup temporary storage
        pendingRegistrations.remove(email);
        request.getSession().removeAttribute("pendingEmail");

        return userRepository.save(newUser);
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
    public void updateEmail(HttpServletRequest request, String newEmail, int otp) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        emailOtpService.verifyOtp(newEmail, otp, "UPDATE_EMAIL");

        user.setEmail(newEmail);
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
