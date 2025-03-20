package com.datingapp.service;

import com.datingapp.entity.User;
import com.datingapp.model.LoginRequest;
import com.datingapp.model.RegisterRequest;
import com.datingapp.model.RegisterResponse;

public interface UserService {

	boolean login(LoginRequest req);

	User register(RegisterResponse req);

	RegisterResponse requestRegister(RegisterRequest req);

}
