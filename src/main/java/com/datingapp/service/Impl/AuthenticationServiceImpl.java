package com.datingapp.service.Impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datingapp.entity.User;

import com.datingapp.model.LoginRequest;
import com.datingapp.model.RegisterRequest;

import com.datingapp.repository.UserRepository;
import com.datingapp.service.AuthenticationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService{
	@Autowired 
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired 
	private VerifyOTPService verifyOtpService;
	
    /**
     * User login
     * OK
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
//        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        boolean passwordMatches = password.equals(user.getPassword());

        if (passwordMatches) {
            log.info("Login successful for email: {}", email);
            return true;
        } else {
            log.warn("Login failed: Incorrect password for email: {}", email);
            return false;
        }
    }
    
    /**
     * New direct registration method without OTP
     */
//    @Transactional
//    public AuthResponse registerUser(RegisterRequest req) {
//        String email = req.getEmail();
//        String fullname = req.fullname();
//        String password = req.getPassword();
//        
//        log.info("Processing direct registration for email: {}", email);
//        
//        // Check if email already exists
//        if (userRepository.findUserByEmail(email).isPresent()) {
//            log.warn("Registration failed: Email already exists: {}", email);
//            throw new IllegalStateException("Email already exists");
//        }
//        
//        // Hash password
////        String hashedPassword = passwordEncoder.encode(password);
//        
//        // Create new user
//        User newUser = User.builder()
//                .email(email)
//                .fullname(fullname)
////                .password(hashedPassword)
//                .password(password)
//                .build();
//        
//        // Save user to database
//        User savedUser = userRepository.save(newUser);
//        log.info("User registered successfully with email: {}", email);
//        
//        return AuthResponse.builder()
//                .success(true)
//                .message("Đăng ký thành công")
//                .data(req)
//                .build();
//    }

    /**
     * Initial registration request - create and send OTP
     */
//    public RegisterResponse requestRegister(RegisterRequest req) {
//        String email = req.getEmail();
//        log.info("Processing registration request for email: {}", email);
//
//        // Check if email already exists
//        if (userRepository.findUserByEmail(email).isPresent()) {
//            log.warn("Registration failed: Email already exists: {}", email);
//            throw new IllegalStateException("Email already exists");
//        }
//
//        // Generate OTP
//        String generatedOtp = verifyOtpService.createOtp(email);
//
//        // Send OTP via email
//        try {
//            emailService.sendOtpEmail(email, generatedOtp);
//            log.info("OTP email sent to: {}", email);
//        } catch (Exception e) {
//            log.error("Failed to send OTP email to: {}", email, e);
//            throw new RuntimeException("Failed to send verification email", e);
//        }
//
//        // Return registration data
//        // Note: In production, you shouldn't return the OTP to the client
//        // It's included here for development purposes
//        return RegisterResponse.builder()
//                .email(email)
//                .otp(generatedOtp)
//                // Remove in production
//                .data(req)
//                .build();
//    }

    /**
     * Complete registration after OTP verification
     */
    @Override
	@Transactional
    public User register(RegisterRequest req) {
      String email = req.getEmail();
      log.info("Processing registration request for email: {}", email);

      // Check if email already exists
      if (userRepository.findUserByEmail(email).isPresent()) {
          log.warn("Registration failed: Email already exists: {}", email);
          throw new IllegalStateException("Email already exists");
      }

      // Generate OTP
      String generatedOtp = verifyOtpService.createOtp(email);

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
      return User.builder()
    		  .email(email)
    		  .password(req.getPassword())
    		  .fullname(req.getFulname())
    		  .phonenumber(req.getPhonenumber())
    		  .build();

//        // Check if email already exists
//        Optional<User> existingUser = userRepository.findUserByEmail(email);
//        if (existingUser.isPresent()) {
//            log.warn("Registration failed: Email already exists: {}", email);
//            throw new IllegalStateException("Email already exists");
//        }
//
//        // Verify OTP
//        boolean isOtpValid = verifyOtpService.verifyOtp(email, otp);
//
//        if (isOtpValid) {
//            // Hash the password before storing
//            //String hashedPassword = passwordEncoder.encode(password);
//
//            // Create new user
//            User newUser = User.builder()
//                    .email(email)
//                    .username(username)
//                    //.password(hashedPassword)
//                    .password(password)
//                    .build();
//
//            // Save user to database
//            User savedUser = userRepository.save(newUser);
//            log.info("User registered successfully with email: {}", email);
//
//            return savedUser;
//        } else {
//            log.warn("Registration failed: Invalid or expired OTP for email: {}", email);
//            throw new IllegalArgumentException("Invalid or expired OTP");
//        }
    }
}
