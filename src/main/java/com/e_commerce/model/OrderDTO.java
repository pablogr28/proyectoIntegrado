package com.e_commerce.model;

import java.time.LocalDate;
import java.util.List;

public class OrderDTO {
    private Long id;
    private String status;
    private LocalDate orderDate;
    private Double total;
    private List<OrderDetailDTO> orderDetails;

    public OrderDTO() {}

    public OrderDTO(Long id, String status, LocalDate orderDate, Double total, List<OrderDetailDTO> orderDetails) {
        this.id = id;
        this.status = status;
        this.orderDate = orderDate;
        this.total = total;
        this.orderDetails = orderDetails;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public List<OrderDetailDTO> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetailDTO> orderDetails) { this.orderDetails = orderDetails; }
}

