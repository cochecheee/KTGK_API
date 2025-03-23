package com.datingapp.service.Impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datingapp.entity.UserOtp;
import com.datingapp.repository.OtpRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VerifyOTPService {
	@Autowired
	private OtpRepository otpRepository;
	
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
    public boolean verifyOtp(String email, String otp) {
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
}
