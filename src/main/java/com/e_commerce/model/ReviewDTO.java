package com.e_commerce.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewDTO {
	
	@NotNull(message = "El producto es obligatorio")
    private Long productId;  

    @NotNull(message = "La valoración es obligatoria")
    @Min(value = 1, message = "La valoración mínima es 1")
    @Max(value = 5, message = "La valoración máxima es 5")
    private Integer rating;

    @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
    private String comment;
    
    

    public ReviewDTO(@NotNull(message = "El producto es obligatorio") Long productId,
			@NotNull(message = "La valoración es obligatoria") @Min(value = 1, message = "La valoración mínima es 1") @Max(value = 5, message = "La valoración máxima es 5") Integer rating,
			@Size(max = 500, message = "El comentario no puede superar los 500 caracteres") String comment) {
		super();
		this.productId = productId;
		this.rating = rating;
		this.comment = comment;
	}
    
    
	public ReviewDTO() {
		super();
	}


	// Getters y setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

}
