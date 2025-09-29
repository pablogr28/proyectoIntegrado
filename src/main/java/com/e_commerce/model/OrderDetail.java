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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "OrderDetail")
public class OrderDetail {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotNull(message="El pedido es obligatorio")
    @ManyToOne
    @JoinColumn(name="OrderId")
    @JsonBackReference
    private Order order;
	
	@NotNull(message="El producto es obligatorio")
    @ManyToOne
    @JoinColumn(name="ProductId")
    @JsonBackReference
    private Product product;
	
	@Min(value = 0, message = "La cantidad mínima es de 0")
    @NotNull(message = "La cantidad del producto es obligatoria")
    @Column(name = "Quantity")
    private Integer quantity;
	
	@DecimalMin(value = "0.0", inclusive = false, message = "El precio total debe ser mayor a 0")
    @Column(name = "UnitPrice")
    private Double unitPrice;

	public OrderDetail(Long id, @NotNull(message = "El pedido es obligatorio") Order order,
			@NotNull(message = "El producto es obligatorio") Product product,
			@Min(value = 0, message = "La cantidad mínima es de 0") @NotNull(message = "La cantidad del producto es obligatoria") Integer quantity,
			@DecimalMin(value = "0.0", inclusive = false, message = "El precio total debe ser mayor a 0") Double unitPrice) {
		super();
		this.id = id;
		this.order = order;
		this.product = product;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
	}

	public OrderDetail() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
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
		OrderDetail other = (OrderDetail) obj;
		return Objects.equals(id, other.id);
	}
	
	

}
