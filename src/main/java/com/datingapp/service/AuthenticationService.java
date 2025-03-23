package com.datingapp.service;

import org.springframework.transaction.annotation.Transactional;

import com.datingapp.entity.User;
import com.datingapp.model.LoginRequest;
import com.datingapp.model.RegisterRequest;

public interface AuthenticationService {

	/**
	 * User login
	 */
	boolean login(LoginRequest req);

	/**
	 * Complete registration after OTP verification
	 */
	User register(RegisterRequest req);

}
