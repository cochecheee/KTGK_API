package com.datingapp.service.Impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import com.datingapp.service.Impl.EmailService;
import com.datingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datingapp.entity.User;
import com.datingapp.entity.UserOtp;
import com.datingapp.model.LoginRequest;
import com.datingapp.model.RegisterRequest;
import com.datingapp.model.RegisterResponse;
import com.datingapp.repository.OtpRepository;
import com.datingapp.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Generate a random 6-digit OTP
     */
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit number
        return String.valueOf(otp);
    }

    /**
     * Create and store OTP for email
     */
    public String createOtp(String email) {
        log.info("Creating new OTP for email: {}", email);

        // Generate new OTP
        String otp = generateOtp();

        // Check if there are previous OTPs and invalidate them
        Optional<UserOtp> existingOtp = otpRepository.findTopByEmailOrderByExpiryTimeDesc(email);
        if (existingOtp.isPresent()) {
            UserOtp otpEntity = existingOtp.get();
            otpEntity.setUsed(true);
            otpRepository.save(otpEntity);
        }

        // Create new OTP entity with 10-minute expiry
        UserOtp userOtp = UserOtp.builder()
                .email(email)
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        // Save to database
        otpRepository.save(userOtp);
        log.info("OTP created and stored for email: {}", email);

        return otp;
    }

    /**
     * Verify if OTP is valid
     */
    private boolean verifyOtp(String email, String otp) {
        log.info("Verifying OTP for email: {}", email);

        // Get the most recent OTP for this email
        Optional<UserOtp> userOtpOpt = otpRepository.findTopByEmailOrderByExpiryTimeDesc(email);

        if (userOtpOpt.isEmpty()) {
            log.warn("No OTP found for email: {}", email);
            return false;
        }

        UserOtp userOtp = userOtpOpt.get();

        // Check if OTP is already used
        if (userOtp.isUsed()) {
            log.warn("OTP for email {} has already been used", email);
            return false;
        }

        // Check if OTP is expired
        if (userOtp.isExpired()) {
            log.warn("OTP for email {} has expired", email);
            return false;
        }

        // Check if OTP matches
        boolean isValid = userOtp.getOtp().equals(otp);

        if (isValid) {
            // Mark OTP as used
            userOtp.setUsed(true);
            otpRepository.save(userOtp);
            log.info("OTP verified successfully for email: {}", email);
        } else {
            log.warn("Invalid OTP entered for email: {}", email);
        }

        return isValid;
    }

    /**
     * User login
     */
    @Override
    public boolean login(LoginRequest req) {
        String email = req.getEmail();
        String password = req.getPassword();

        log.info("Attempting login for email: {}", email);

        Optional<User> userOpt = userRepository.findUserByEmail(email);
        if (userOpt.isEmpty()) {
            log.warn("Login failed: Email not found: {}", email);
            return false;
        }

        User user = userOpt.get();
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());

        if (passwordMatches) {
            log.info("Login successful for email: {}", email);
            return true;
        } else {
            log.warn("Login failed: Incorrect password for email: {}", email);
            return false;
        }
    }

    /**
     * Initial registration request - create and send OTP
     */
    @Override
    public RegisterResponse requestRegister(RegisterRequest req) {
        String email = req.getEmail();
        log.info("Processing registration request for email: {}", email);

        // Check if email already exists
        if (userRepository.findUserByEmail(email).isPresent()) {
            log.warn("Registration failed: Email already exists: {}", email);
            throw new IllegalStateException("Email already exists");
        }

        // Generate OTP
        String generatedOtp = createOtp(email);

        // Send OTP via email
        try {
            emailService.sendOtpEmail(email, generatedOtp);
            log.info("OTP email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", email, e);
            throw new RuntimeException("Failed to send verification email", e);
        }

        // Return registration data
        // Note: In production, you shouldn't return the OTP to the client
        // It's included here for development purposes
        return RegisterResponse.builder()
                .email(email)
                .username(req.getUsername())
                // Remove in production
                .build();
    }

    /**
     * Complete registration after OTP verification
     */
    @Override
    @Transactional
    public User register(RegisterResponse req) {
        String email = req.getEmail();
        String username = req.getUsername();
        String password = req.getPassword();
        String otp = req.getOtp();

        log.info("Completing registration for email: {}", email);

        // Check if email already exists
        Optional<User> existingUser = userRepository.findUserByEmail(email);
        if (existingUser.isPresent()) {
            log.warn("Registration failed: Email already exists: {}", email);
            throw new IllegalStateException("Email already exists");
        }

        // Verify OTP
        boolean isOtpValid = verifyOtp(email, otp);

        if (isOtpValid) {
            // Hash the password before storing
            String hashedPassword = passwordEncoder.encode(password);

            // Create new user
            User newUser = User.builder()
                    .email(email)
                    .username(username)
                    .password(hashedPassword)
                    .build();

            // Save user to database
            User savedUser = userRepository.save(newUser);
            log.info("User registered successfully with email: {}", email);

            return savedUser;
        } else {
            log.warn("Registration failed: Invalid or expired OTP for email: {}", email);
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
    }
}