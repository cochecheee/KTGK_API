package com.datingapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.datingapp.model.LoginRequest;
import com.datingapp.model.RegisterRequest;
import com.datingapp.service.AuthenticationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AuthController {
	
	@Autowired
	private AuthenticationService authService;
	
	@PostMapping("/api/auth/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest req) {
		boolean isSuccess = authService.login(req);
		if(isSuccess == true) {
			return ResponseEntity.ok(req);
		}else {
			return ResponseEntity.ok("Đăng nhập thất bại");
		}
	}
	
//	@PostMapping("/api/auth/request-register")
//	public ResponseEntity<?> requestRegister(@RequestBody RegisterRequest req){
//		return ResponseEntity.ok(authService.requestRegister(req));
//	}
	
	@PostMapping("/api/auth/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
		return ResponseEntity.ok(authService.register(req));
	}
}
