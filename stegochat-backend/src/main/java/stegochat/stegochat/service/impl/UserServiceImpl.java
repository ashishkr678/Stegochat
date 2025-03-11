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
import stegochat.stegochat.security.EncryptionUtil;
import stegochat.stegochat.security.JwtUtil;
import stegochat.stegochat.service.EmailOtpService;
import stegochat.stegochat.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

        if (userRepository.existsByEmail(userDTO.getEmail())
                || pendingRegistrationRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already registered or pending verification.");
        }
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken.");
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

        emailOtpService.sendOtp(userDTO.getEmail(), "REGISTER");

    }

    @Override
    @Transactional
    public UserDTO completeRegistration(String email, String otp) {
        Optional<OtpEntity> otpEntityOpt = otpRepository.findByEmailAndType(email, "REGISTER");

        if (otpEntityOpt.isEmpty()) {
            throw new BadRequestException("No OTP found for this email. Please request OTP again.");
        }

        OtpEntity otpEntity = otpEntityOpt.get();

        if (otpEntity.isExpired()) {
            otpRepository.delete(otpEntity);
            throw new BadRequestException("OTP expired. Please request a new one.");
        }

        if (!passwordEncoder.matches(otp, otpEntity.getOtp())) {
            throw new BadRequestException("Incorrect OTP. Please try again.");
        }

        Optional<PendingRegistrationEntity> pendingUserOpt = pendingRegistrationRepository.findByEmail(email);

        if (pendingUserOpt.isEmpty()) {
            throw new BadRequestException("No pending registration found. Please register again.");
        }

        PendingRegistrationEntity pendingUser = pendingUserOpt.get();

        UsersEntity newUser = UserMapper.toEntity(pendingUser);
        newUser.setCreatedAt(LocalDateTime.now());

        // ✅ Step 1: Generate a base encryption key for this user
        String baseEncryptionKey = EncryptionUtil.generateBaseEncryptionKey();

        // ✅ Step 2: Store the base key in `encryptionKeys`
        newUser.getEncryptionKeys().put("baseKey", baseEncryptionKey);

        newUser.setMetadata(Map.of(
                "createdAt", LocalDateTime.now(),
                "accountStatus", "ACTIVE"));

        userRepository.save(newUser);

        pendingRegistrationRepository.deleteByEmail(email);
        otpRepository.deleteByEmailAndType(email, "REGISTER");

        return UserMapper.toDTO(newUser);
    }

    @Override
    public UserDTO loginUser(String username, String password, HttpServletRequest request,
            HttpServletResponse response) {
        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password.");
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

    @Override
    public void initiateEmailUpdate(HttpServletRequest request, String newEmail) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        if (userRepository.existsByEmail(newEmail)) {
            throw new BadRequestException("This email is already registered.");
        }

        Optional<UsersEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new BadRequestException("User not found.");
        }

        emailOtpService.sendOtp(newEmail, "EMAIL_UPDATE");
    }

    @Override
    public void verifyEmailUpdate(HttpServletRequest request, int otp) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        String otpString = String.valueOf(otp);

        Optional<UsersEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new BadRequestException("User not found.");
        }

        UsersEntity user = userOpt.get();
        String newEmail = user.getEmail();

        emailOtpService.verifyOtp(newEmail, otpString, "EMAIL_UPDATE");

        user.setEmail(newEmail);
        userRepository.save(user);

        otpRepository.deleteByEmailAndType(newEmail, "EMAIL_UPDATE");
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String username = CookieUtil.extractUsernameFromCookie(request);

        if (username == null) {
            throw new UnauthorizedException("User not logged in.");
        }

        UsersEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found."));

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
