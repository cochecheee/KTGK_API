package com.datingapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.datingapp.model.LoginRequest;
import com.datingapp.model.RegisterRequest;
import com.datingapp.model.RegisterResponse;
import com.datingapp.service.UserService;

@RestController
public class AuthController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/api/auth/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest req) {
		boolean isSuccess = userService.login(req);
		if(isSuccess == true) {
			return ResponseEntity.ok(req);
		}else {
			return ResponseEntity.ok("Đăng nhập thất bại");
		}
	}
	
	@PostMapping("/api/auth/request-register")
	public ResponseEntity<?> requestRegister(@RequestBody RegisterRequest req){
		return ResponseEntity.ok(userService.requestRegister(req));
	}
	
	@PostMapping("/api/auth/register")
	public ResponseEntity<?> register(@RequestBody RegisterResponse req) {
		return ResponseEntity.ok(userService.register(req));
	}
}
