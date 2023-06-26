package com.moviebooking.auth.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.moviebooking.auth.exception.InvalidInputException;

import com.moviebooking.auth.model.User;
import com.moviebooking.auth.payload.LoginRequest;
import com.moviebooking.auth.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {

	@Value("${authentication.app.jwtSecret}")
	private String jwtSecret;

	@Value("${authentication.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Autowired
	UserService userService;

	public String generatToken(LoginRequest loginRequest) {
		try {
			// checks
			String username = loginRequest.getUsername();
			String password = loginRequest.getPassword();
//			loginRequest.getPassword();
			if (username == null || password == null) {
				throw new InvalidInputException("please enter valid credentials");
			}
//			boolean flag = userService.loginUser(loginRequest);// validating the user here only
//			Optional<User> user = userService.getUserByUsername(username);// for getting the role from user to iterate
//			List<ERole> roleNames = user.get().getRoles().stream().map(Role::getName).collect(Collectors.toList());
//			System.out.println(roleNames.get(0));
//			System.out.println("above is role");
//			if (!flag) {
//				throw new InvalidInputException("Invalid credentials");
//			}
			// Generating jwt token here...
//			String jwtToken = Jwts.builder().setSubject(username).claim("role", roleNames.get(0).toString())
//					.setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
//					.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
			
			Map<String, Object> claims = new HashMap<>();
			
			String jwtToken=doGenerateToken(claims, loginRequest.getUsername());
			return jwtToken;
		} catch (InvalidInputException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}
	
	private String doGenerateToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}
	
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
	}


	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public Claims getJwtClaims(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
		return claims;
	}

	// will need this later
	public String getRoleFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

		String role = (String) claims.get("role");

		return role;
	}

	public boolean validateJwtToken(String token) {// fix me
		String authToken = null;
		String user = null;
		if (token != null && token.startsWith("Bearer ")) {
			authToken = token.substring(7);
			try {
				user = getUserNameFromJwtToken(authToken);
				System.out.println(user);// fix
				return true;
			} catch (SignatureException e) {
				System.err.println("Invalid JWT signature: {} " + e.getMessage());
				
			} catch (MalformedJwtException e) {
				
				System.err.println("Invalid JWT signature: {} " + e.getMessage());
			} catch (ExpiredJwtException e) {
			
				System.err.println("JWT token is expired: {} " + e.getMessage());
			} catch (UnsupportedJwtException e) {
			
				System.err.println("JWT token is unsupported: {} " + e.getMessage());
			} catch (IllegalArgumentException e) {
				
				System.err.println("JWT claims string is empty: {} " + e.getMessage());
			}
		}

		return false;
	}

}
