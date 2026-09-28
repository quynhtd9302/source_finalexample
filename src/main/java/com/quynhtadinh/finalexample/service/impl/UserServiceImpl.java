package com.quynhtadinh.finalexample.service.impl;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.quynhtadinh.finalexample.entity.Role;
import com.quynhtadinh.finalexample.entity.User;
import com.quynhtadinh.finalexample.repository.RoleRepository;
import com.quynhtadinh.finalexample.repository.UserRepository;
import com.quynhtadinh.finalexample.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public void save(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		Role userRole = roleRepository.findByName("ROLE_USER");
		HashSet<Role> roles = new HashSet<>();
		if (userRole != null) {
			roles.add(userRole);
		}
		user.setRoles(roles);
		userRepository.save(user);
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public Page<User> findAll(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@Override
	public User insert(User user) {
		return userRepository.save(user);
	}

	@Override
	public boolean delete(long id) {
		Optional<User> user = userRepository.findById(id);
		if (user.isPresent()) {
			userRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public User update(User user) {
		User existingUser = userRepository.findById(user.getId()).orElse(null);
		if (existingUser == null) {
			return null;
		}
		existingUser.setUsername(user.getUsername());
		existingUser.setEmail(user.getEmail());
		if (user.getPassword() != null && !user.getPassword().isBlank()) {
			existingUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		}
		return userRepository.save(existingUser);
	}

	@Override
	public User findById(long id) {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public Page<User> searchUsers(Optional<String> keyword, Pageable pageable) {
		return userRepository.findAllByUsername(keyword, pageable);
	}
}
