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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Payment")
public class Payment {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NotNull(message="El pedido es obligatorio")
    @ManyToOne
    @JoinColumn(name="OrderId")
    @JsonBackReference
    private Order order;
	
	@NotBlank(message = "El método de pago es obligatorio")
    @Column(name = "PaymentMethod")
    private String paymentMethod;
	
	@DecimalMin(value = "0.0", inclusive = false, message = "El precio total del pago debe ser mayor a 0")
    @NotNull(message = "El precio total del pago es obligatorio")
    @Column(name = "Amount")
    private Double amount;
	
	@Column(name = "PaymentDate")
	private LocalDate paymentDate;
	 
    @Column(name = "PaymentStatus")
    private String paymentStatus;

	public Payment(Long id, @NotNull(message = "El pedido es obligatorio") Order order,
			@NotBlank(message = "El método de pago es obligatorio") String paymentMethod,
			@DecimalMin(value = "0.0", inclusive = false, message = "El precio total del pago debe ser mayor a 0") @NotNull(message = "El precio total del pago es obligatorio") Double amount,
			LocalDate paymentDate, String paymentStatus) {
		super();
		this.id = id;
		this.order = order;
		this.paymentMethod = paymentMethod;
		this.amount = amount;
		this.paymentDate = LocalDate.now();
		this.paymentStatus = paymentStatus;
	}

	public Payment() {
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

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
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
		Payment other = (Payment) obj;
		return Objects.equals(id, other.id);
	}
	
	
    
    

}
