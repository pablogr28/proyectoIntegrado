package com.e_commerce.model;

import java.util.Objects;
import java.util.Date;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "OrderDetail")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Relación con Order: opcional, null mientras está en carrito
    @ManyToOne
    @JoinColumn(name = "OrderId", nullable = true)
    @JsonBackReference
    private Order order;

    @ManyToOne
    @JoinColumn(name = "ProductId")
    @JsonManagedReference(value = "product-orderDetails")
    private Product product;


    // 🔹 Relación con User: para saber a quién pertenece el carrito
    @ManyToOne
    @JoinColumn(name = "UserId", nullable = true)
    private User user;

    @Column(name = "ProductName")
    private String productName;

    @Min(value = 0, message = "La cantidad mínima es de 0")
    @NotNull(message = "La cantidad del producto es obligatoria")
    @Column(name = "Quantity")
    private Integer quantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0")
    @Column(name = "UnitPrice")
    private Double unitPrice;

    // 🔹 Constructores
    public OrderDetail() {}

    public OrderDetail(Long id, Order order, Product product, User user,
                       String productName, Integer quantity, Double unitPrice) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.user = user;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // 🔹 Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrderDetail other = (OrderDetail) obj;
        return Objects.equals(id, other.id);
    }
}
