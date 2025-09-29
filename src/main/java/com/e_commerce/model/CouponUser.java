package com.e_commerce.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "CouponUser")
public class CouponUser {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotNull(message="El cupón es obligatorio")
    @ManyToOne
    @JoinColumn(name="CouponId")
    @JsonBackReference
    private Coupon coupon;
	
	@NotNull(message="El usuario es obligatorio")
    @ManyToOne
    @JoinColumn(name="UserId")
    @JsonBackReference
    private User user;
	
	@NotNull(message="El pedido es obligatorio")
    @ManyToOne
    @JoinColumn(name="OrderId")
    @JsonBackReference
    private Order order;

	public CouponUser(Long id, @NotNull(message = "El cupón es obligatorio") Coupon coupon,
			@NotNull(message = "El usuario es obligatorio") User user,
			@NotNull(message = "El pedido es obligatorio") Order order) {
		super();
		this.id = id;
		this.coupon = coupon;
		this.user = user;
		this.order = order;
	}

	public CouponUser() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
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
		CouponUser other = (CouponUser) obj;
		return Objects.equals(id, other.id);
	}
	
	

}
