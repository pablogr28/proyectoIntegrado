package com.e_commerce.model;

import java.time.LocalDate;

public class ReviewDTO {

    private Long id;

    // Datos del usuario
    private Long userId;
    private String userName;

    // Datos del producto
    private Long productId;
    private String productName;

    private Integer rating;
    private String comment;
    private LocalDate reviewDate;

    // 🔹 Constructor vacío obligatorio para Jackson
    public ReviewDTO() {}

    // Constructor a partir de entidad Review
    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.userName = review.getUser().getName();
        this.productId = review.getProduct().getId();
        this.productName = review.getProduct().getName();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.reviewDate = review.getReviewDate();
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }
}
