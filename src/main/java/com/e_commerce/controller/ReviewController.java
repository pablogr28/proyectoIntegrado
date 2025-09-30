package com.e_commerce.controller;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.e_commerce.model.Product;
import com.e_commerce.model.Review;
import com.e_commerce.model.ReviewDTO;
import com.e_commerce.model.User;
import com.e_commerce.service.ProductService;
import com.e_commerce.service.ReviewService;
import com.e_commerce.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 404,
                    "error", "Not Found",
                    "message", "Review con ID " + id + " no encontrada."
                )
            );
        }
        return ResponseEntity.ok(review);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createReview(@PathVariable Long userId, @RequestBody @Valid ReviewDTO reviewDto) {
        User user = userService.getUserById(userId).orElseThrow();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("message", "El usuario con ID " + userId + " no existe.")
            );
        }

        Product product = productService.getProductById(reviewDto.getProductId());
        if (product == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("message", "El producto con ID " + reviewDto.getProductId() + " no existe.")
            );
        }

        // Construir review
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        review.setReviewDate(LocalDate.now());

        // Validar duplicado (opcional según tu lógica de negocio)
        if (reviewService.getReviewById(review.getId()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("message", "El usuario ya ha realizado una review para este producto.")
            );
        }

        Review saved = reviewService.saveReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody @Valid ReviewDTO reviewDto) {
        Review existing = reviewService.getReviewById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 404,
                    "error", "Not Found",
                    "message", "No se encontró la review con ID " + id
                )
            );
        }

        Product product = productService.getProductById(reviewDto.getProductId());
        if (product == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("message", "El producto con ID " + reviewDto.getProductId() + " no existe.")
            );
        }

        existing.setProduct(product);
        existing.setRating(reviewDto.getRating());
        existing.setComment(reviewDto.getComment());

        Review updated = reviewService.saveReview(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        Review existing = reviewService.getReviewById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 404,
                    "error", "Not Found",
                    "message", "No se encontró la review con ID " + id
                )
            );
        }
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
