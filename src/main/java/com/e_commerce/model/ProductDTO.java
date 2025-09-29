package com.e_commerce.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductDTO {

	@NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @Positive(message = "El precio debe ser positivo")
    private Double price;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock mínimo es 0")
    private Integer stock;

    private Boolean available = true;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId; // solo enviamos el id de categoría

    private String imageUrl; 
    
    

    public ProductDTO(@NotBlank(message = "El nombre del producto es obligatorio") String name,
			@NotBlank(message = "La descripción es obligatoria") String description,
			@Positive(message = "El precio debe ser positivo") Double price,
			@NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock mínimo es 0") Integer stock,
			Boolean available, @NotNull(message = "La categoría es obligatoria") Long categoryId, String imageUrl) {
		super();
		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.available = available;
		this.categoryId = categoryId;
		this.imageUrl = imageUrl;
	}
    
    
	public ProductDTO() {
		super();
	}


	// Getters y setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
