package com.e_commerce.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name="Product")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(name = "Name")
    private String name;

    @NotBlank(message = "La descripción del producto es obligatoria")
    @Column(name = "Description")
    private String description;

    @Positive(message="El precio del producto debe ser positivo")
    @Column(name="Price")
    private Double price;

    @NotNull(message="El stock del producto es obligatorio")
    @Min(value=0,message="El stock mínimo es 0")
    @Column(name="Stock")
    private Integer stock;

    @Column(name = "Available")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name="CategoryId")
    @JsonIgnoreProperties("products") 
    private Category category;
    
    @Column(name="ImagePublicId")
    private String imagePublicId;

    // **Nuevo atributo para la imagen**
    @Column(name="Image")
    private String image;

    @OneToMany(mappedBy = "product")
    @JsonBackReference(value = "product-reviews")
    private List<Review> reviews;

    @OneToMany(mappedBy = "product")
    @JsonBackReference(value = "product-notifications")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "product")
    @JsonBackReference(value = "product-orderDetails")
    private List<OrderDetail> orderDetails;


    // Constructores
    public Product() {}

    public Product(Long id, String name, String description, Double price, Integer stock, Boolean available, Category category, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.available = available;
        this.category = category;
        this.image = image;
    }

    // Getters y setters
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public String getImagePublicId() {
		return imagePublicId;
	}

	public void setImagePublicId(String imagePublicId) {
		this.imagePublicId = imagePublicId;
	}

    
}
