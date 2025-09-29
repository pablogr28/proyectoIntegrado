package com.e_commerce.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentDTO {

	@NotNull(message = "El pedido es obligatorio")
    private Long orderId;

    @NotBlank(message = "El método de pago es obligatorio")
    private String paymentMethod;

    // No enviamos amount, paymentDate ni paymentStatus, el backend los asigna automáticamente

    

    public Long getOrderId() {
        return orderId;
    }

    public PaymentDTO(@NotNull(message = "El pedido es obligatorio") Long orderId,
			@NotBlank(message = "El método de pago es obligatorio") String paymentMethod) {
		super();
		this.orderId = orderId;
		this.paymentMethod = paymentMethod;
	}
    
    

	public PaymentDTO() {
		super();
	}

	public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
