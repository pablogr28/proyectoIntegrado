package com.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_commerce.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
