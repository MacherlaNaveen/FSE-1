package com.moviebooking.auth.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moviebooking.auth.exception.InvalidInputException;
import com.moviebooking.auth.exception.JwtGenerationException;
import com.moviebooking.auth.model.User;
import com.moviebooking.auth.payload.JwtResponse;
import com.moviebooking.auth.payload.LoginRequest;
import com.moviebooking.auth.payload.ResetRequest;
import com.moviebooking.auth.payload.SignupRequest;

import com.moviebooking.auth.repository.UserRepository;
import com.moviebooking.auth.security.JwtUtils;
import com.moviebooking.auth.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1.0/auth")
@CrossOrigin("*")
public class AuthController {

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	

	@Autowired
	JwtUtils jwtUtils;

	

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
//		return userService.addUser(signUpRequest);
		ResponseEntity<?> response = userService.addUser(signUpRequest);

	    if (response.getStatusCode().equals(HttpStatus.OK)) {
	        // Registration successful
	        return ResponseEntity.ok().body(response.getBody());
	    } else {
	        // Registration failed
	        return ResponseEntity.badRequest().body(response.getBody());
	    }
	}

	

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) throws JwtGenerationException {
		
		try {
			String jwtToken = jwtUtils.generatToken(loginRequest);
			// sending custom message here
			Optional<User> userDetails = userService.getUserByUsername(loginRequest.getUsername());// fetch the user
			if (userDetails.isPresent() && userDetails.get().getPassword().equals(loginRequest.getPassword())) {
				return ResponseEntity.ok(new JwtResponse(jwtToken, userDetails.get().getId(),
						userDetails.get().getUsername(), userDetails.get().getEmail(), userDetails.get().getRole()));
			} else {
				throw new InvalidInputException("Invalid Credentials");
			}
		} catch (InvalidInputException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}

	
	@PostMapping("/validate")
	public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
		System.out.println("tokens"+token);
		if (jwtUtils.validateJwtToken(token)) {
			// return ResponseEntity.ok("Valid token");
			System.out.println("Testing For validation");
			Map<String, String> userInfo = new HashMap<>();
			String authToken = token.substring(7);
			String username = jwtUtils.getUsernameFromToken(authToken);
			String role = userService.getrole(username);
			System.out.println("username:"+username);
			System.out.println("role:"+role);
			userInfo.put(username, role);
			return ResponseEntity.status(HttpStatus.OK).body(userInfo);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
		}
	}

	@GetMapping("/getrole") 
	@Operation(security = @SecurityRequirement(name = "bearer-token"))
	public String getRole(@RequestHeader("Authorization") String token) {
		String authToken = token.substring(7);
		System.err.println(authToken + " " + "this is token");
		String username=jwtUtils.getUsernameFromToken(authToken);
		return userService.getrole(username);
	}

	@PatchMapping("/forgot/{username}/updatepassword") 
	public ResponseEntity<?> forgotPassword(@PathVariable(name = "username") String username,
			@RequestBody ResetRequest resetRequest) {
		return userService.updatePassword(username, resetRequest);

	}

}
