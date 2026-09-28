package com.quynhtadinh.finalexample.service;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.quynhtadinh.finalexample.entity.User;


public interface UserService {
	void save(User user);

	User findByUsername(String username);

	Page<User> findAll(Pageable pageable);

	User insert(User user);

	boolean delete(long id);

	User update(User user);

	User findById(long id);

	Page<User> searchUsers(Optional<String> keyword, Pageable pageable);
	
}