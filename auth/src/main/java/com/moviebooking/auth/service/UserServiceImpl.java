package com.moviebooking.auth.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.moviebooking.auth.exception.InvalidInputException;

import com.moviebooking.auth.model.User;
import com.moviebooking.auth.payload.LoginRequest;
import com.moviebooking.auth.payload.ResetRequest;
import com.moviebooking.auth.payload.SignupRequest;

import com.moviebooking.auth.repository.UserRepository;


@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	

	// for registration
	@Override
	public ResponseEntity<?> addUser(SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return new ResponseEntity<>("Username is already exists!", HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity<>("Email is already exists!", HttpStatus.BAD_REQUEST);
		}
		// setting properties in constructor itself
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword(),
				signUpRequest.getSecurityQuestion(), signUpRequest.getSecurityAnswer(),signUpRequest.getRole());

		

		
		userRepository.save(user);
		
		return ResponseEntity.ok().body("{\"message\": \"User registered successfully\"}");
	}

//	// for login
//	@Override
//	public boolean loginUser(LoginRequest loginRequest) {
//		// TODO this one is for authentication
//		Optional<User> validateUser = userRepository.validateUser(loginRequest.getUsername(),
//				loginRequest.getPassword());
//		System.err.println(validateUser.get());
//		Optional<User> userObj = userRepository.findByUsername(loginRequest.getUsername());
//		if (userObj.isPresent() && userObj.get().getPassword().equals(loginRequest.getPassword())) {
//			return true;
//		}
//		return false;
//	}

	// for updating the password
	@Override
	public ResponseEntity<?> updatePassword(String username, ResetRequest resetRequest) {// fix--me no need of username
																							// here
		if (!userRepository.existsByUsername(resetRequest.getUsername())) {
			return new ResponseEntity<>("Username doesn't exists!", HttpStatus.BAD_REQUEST);
		}
		Optional<User> userdata = userRepository.findByUsername(resetRequest.getUsername());
		if (resetRequest.getSecQuestion().equals(userdata.get().getSecurityQuestion())
				&& resetRequest.getSecAnswer().equals(userdata.get().getSecurityAnswer())) {
			userdata.get().setPassword(resetRequest.getNewPassword());
			userRepository.save(userdata.get());
			return new ResponseEntity<>("changed password successfully!", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>("could not update password(cause:sec ques not match)!", HttpStatus.BAD_REQUEST);

	}

	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<User> getUserByUsername(String username) {
		// TODO 
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isEmpty()) {
			throw new InvalidInputException("username is not present");
		}
		return user;
	}
	@Override
	public String getrole(String username) {
		
		Optional<User> userObj = userRepository.findByUsername(username);
		return userObj.get().getRole();
		
	}

}
