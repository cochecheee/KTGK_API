package com.datingapp.service;

import com.datingapp.entity.User;

public interface UserService {

	<S extends User> S save(S entity);

}