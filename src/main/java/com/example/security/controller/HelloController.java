package com.example.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class HelloController {

	@GetMapping("/")
	public String getMethodName(HttpServletRequest request) {
		return new String("Hello world " + request.getSession().getId());
	}
	
}
