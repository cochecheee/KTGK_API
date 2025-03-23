package com.datingapp.service.Impl;

import com.datingapp.entity.User;
import com.datingapp.repository.UserRepository;
import com.datingapp.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public <S extends User> S save(S entity) {
		return userRepository.save(entity);
	}
	
    
}