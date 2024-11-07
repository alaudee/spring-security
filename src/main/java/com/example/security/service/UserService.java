package com.example.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.security.entity.Users;
import com.example.security.repo.UserRepo;

@Service
public class UserService {

	@Autowired
	private UserRepo repo;
	
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private JWTService jwtService;
	
	// Cifra d crypto default da lib do spring security
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	
	public Users register(Users user) {
		user.setPassword(encoder.encode(user.getPassword()));
		
		return repo.save(user);
	}

	public String verify(Users user) {
		/* Instanciando as mesmas configurações ja definidas do auth manager
		 * a app verifica utilizando esse filterse aquela usuario e 
		 * senha são validos
		 */
		Authentication authentication = authManager.
				authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
		
		if (authentication.isAuthenticated()) {
			return jwtService.generateToken(user.getUsername());
		}
		
		return "fail";
	}
}
