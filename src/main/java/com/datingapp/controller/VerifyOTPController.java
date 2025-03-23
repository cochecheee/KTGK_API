package com.datingapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.datingapp.entity.User;
import com.datingapp.model.OTPRequest;
import com.datingapp.service.UserService;
import com.datingapp.service.Impl.VerifyOTPService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class VerifyOTPController {
	@Autowired
	private VerifyOTPService verifyOtpService;
	@Autowired
	private UserService userService;
	
	@PostMapping("/api/auth/verify-otp")
	@Transactional
	public ResponseEntity<User> verifyOtp(@RequestBody OTPRequest otpRequest) {
		boolean isVerified = verifyOtpService.verifyOtp(otpRequest.getData().getEmail(), otpRequest.getOtp());
		if (isVerified) {
			// save user
			// Tạo người dùng mới
            User newUser = User.builder()
                    .email(otpRequest.getData().getEmail())
                    .fullname(otpRequest.getData().getFulname())
                    .password(otpRequest.getData().getPassword()) // Nên mã hóa mật khẩu
                    .phonenumber(otpRequest.getData().getPhonenumber())
                    .build();
            userService.save(newUser);
            log.info("User registered successfully with email: {}", otpRequest.getData().getEmail());
			// return user
            return ResponseEntity.ok(newUser);
		}
		return ResponseEntity.badRequest().body(null);
	}
}
