package com.e_commerce.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
	
	@NotBlank(message = "El nombre del producto el obligatorio")
    @Column(name = "Name")
    private String name;
	
	@NotBlank(message = "La descripción del producto el obligatorio")
    @Column(name = "Description")
    private String description;
	
	@Positive(message="El precio del producto debe ser positivo")
	@Column(name="Price")
	private Double price;
	
	@NotNull(message="El stock del producto debe ser obligatorio")
	@Min(value=0,message="Los stock del producto debe ser como mínimo 0")
	@Column(name="Stock")
	private Integer stock;
	
    @Column(name = "Available")
    private String available;
    
    @NotNull(message="La categoria del producto es obligatoria")
    @ManyToOne
    @JoinColumn(name="CategoryId")
    @JsonBackReference
    private Category category;
   
    
    @OneToMany(mappedBy="product")
    @JsonManagedReference
    public List<Review> reviews;
    
    @OneToMany(mappedBy="product")
    @JsonManagedReference
    public List<Notification> notifications;
    
    @OneToMany(mappedBy="product")
    @JsonManagedReference
    public List<OrderDetail> orderDetails;

	public Product(Long id, @NotBlank(message = "El nombre del producto el obligatorio") String name,
			@NotBlank(message = "La descripción del producto el obligatorio") String description,
			@Positive(message = "El precio del producto debe ser positivo") Double price,
			@NotNull(message = "El stock del producto debe ser obligatorio") @Min(value = 0, message = "Los stock del producto debe ser como mínimo 0") Integer stock,
			String available, @NotNull(message = "La categoria del producto es obligatoria") Category category) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.available = available;
		this.category = category;
	}

	public Product() {
		super();
	}

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

	public String getAvailable() {
		return available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
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
		Product other = (Product) obj;
		return Objects.equals(id, other.id);
	}
    
    

}
