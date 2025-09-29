package com.e_commerce.model;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Review")
public class Review {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotNull(message="El usuario es obligatorio")
    @ManyToOne
    @JoinColumn(name="UserId")
    @JsonBackReference
    private User user;
	
	@NotNull(message="El producto es obligatorio")
    @ManyToOne
    @JoinColumn(name="ProductId")
    @JsonBackReference
    private Product product;
	
	@Min(value = 1, message = "La valoración mínima es 1")
	@Min(value = 5, message = "La valoración máxima es 5")
    @NotNull(message = "La valoración es obligatoria")
    @Column(name = "Rating")
    private Integer rating;
	
    @Column(name = "Comment")
    private String comment;
    
    @Column(name = "ReviewDate")
    private LocalDate reviewDate;

	public Review(Long id, @NotNull(message = "El usuario es obligatorio") User user,
			@NotNull(message = "El producto es obligatorio") Product product,
			@Min(value = 1, message = "La valoración mínima es 1") @Min(value = 5, message = "La valoración máxima es 5") @NotNull(message = "La valoración es obligatoria") Integer rating,
			String comment) {
		super();
		this.id = id;
		this.user = user;
		this.product = product;
		this.rating = rating;
		this.comment = comment;
		this.reviewDate = LocalDate.now();
	}

	public Review() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDate getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(LocalDate reviewDate) {
		this.reviewDate = reviewDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Review other = (Review) obj;
		return Objects.equals(id, other.id);
	}
	
	
    
    

}
