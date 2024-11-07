package com.example.security.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security.entity.Users;

public interface UserRepo extends JpaRepository<Users, Integer>{

	Users findByUsername(String username);
}
