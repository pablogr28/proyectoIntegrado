package com.e_commerce.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.e_commerce.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	    boolean existsByUsername(String username);
	    
	    Optional<User> findByUsername(String username);
	    Optional<User> findByEmail(String email);
	    Optional<User> findById(Integer id);
}
