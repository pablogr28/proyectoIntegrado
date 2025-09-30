package com.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_commerce.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
	
	boolean existsByEmail (String email);
	boolean existsByUsername(String username);

}
