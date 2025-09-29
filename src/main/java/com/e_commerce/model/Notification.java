package com.e_commerce.model;

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
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Notification")
public class Notification {
	
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
	
	@Column(name = "Sent")
    private boolean sent;

	public Notification(Long id, @NotNull(message = "El usuario es obligatorio") User user,
			@NotNull(message = "El producto es obligatorio") Product product, boolean sent) {
		super();
		this.id = id;
		this.user = user;
		this.product = product;
		this.sent = sent;
	}

	public Notification() {
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

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
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
		Notification other = (Notification) obj;
		return Objects.equals(id, other.id);
	}
	
	

}
