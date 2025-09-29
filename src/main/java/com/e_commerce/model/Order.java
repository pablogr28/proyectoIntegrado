package com.e_commerce.model;

import java.time.LocalDate;
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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "OrderTable")
public class Order {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotNull(message="El usuario es obligatorio")
    @ManyToOne
    @JoinColumn(name="UserId")
    @JsonBackReference
    private User user;
	
	@Column(name = "OrderDate")
    private LocalDate orderDate;
	
	@DecimalMin(value = "0.0", inclusive = false, message = "El precio del pedido debe ser mayor a 0")
    @NotNull(message = "El precio del pedido es obligatorio")
    @Column(name = "Total")
    private Double total;
	
	@Column(name = "Status")
    private String status;
	
	@OneToMany(mappedBy="order")
    @JsonManagedReference
    public List<CouponUser> coupons;
	
	@OneToMany(mappedBy="order")
    @JsonManagedReference
    public List<OrderDetail> orderDetails;

	public Order(Long id, @NotNull(message = "El usuario es obligatorio") User user, LocalDate orderDate,
			@DecimalMin(value = "0.0", inclusive = false, message = "El precio del pedido debe ser mayor a 0") @NotNull(message = "El precio del pedido es obligatorio") Double total,
			String status) {
		super();
		this.id = id;
		this.user = user;
		this.orderDate = LocalDate.now();
		this.total = total;
		this.status = status;
	}

	public Order() {
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

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
		Order other = (Order) obj;
		return Objects.equals(id, other.id);
	}
	
	
	
	

}
