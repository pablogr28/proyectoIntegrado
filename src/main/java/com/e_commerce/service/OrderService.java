package com.e_commerce.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce.model.Order;
import com.e_commerce.model.OrderDTO;
import com.e_commerce.model.OrderDetailDTO;
import com.e_commerce.model.OrderDetail;
import com.e_commerce.model.User;
import com.e_commerce.repository.OrderDetailRepository;
import com.e_commerce.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    /**
     * ✅ CONFIRMAR PEDIDO (checkout)
     */
    public OrderDTO confirmOrder(User user) {
        List<OrderDetail> cartItems = orderDetailRepository.findByUserAndOrderIsNull(user);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        // Crear nueva orden
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setStatus("CONFIRMED");
        orderRepository.save(order);

        // Asignar los OrderDetail al pedido y calcular total
        double total = 0;
        for (OrderDetail detail : cartItems) {
            detail.setOrder(order);
            total += detail.getQuantity() * detail.getUnitPrice();
            orderDetailRepository.save(detail);
        }

        order.setTotal(total);
        orderRepository.save(order);

        return mapToDTO(order);
    }

    /**
     * ✅ OBTENER TODAS LAS ÓRDENES DE UN USUARIO como DTO
     */
    public List<OrderDTO> getOrdersByUser(User user) {
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * ✅ OBTENER UNA ORDEN ESPECÍFICA como DTO
     */
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
        return mapToDTO(order);
    }
    
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return orderRepository.existsByUserIdAndOrderDetailsProductId(userId, productId);
    }


    /**
     * 🔹 Helper para mapear entidad a DTO
     */
    private OrderDTO mapToDTO(Order order) {
        List<OrderDetailDTO> details = order.getOrderDetails().stream().map(d ->
            new OrderDetailDTO(
                d.getId(),
                d.getProduct() != null ? d.getProduct().getId() : null,
                d.getProductName(),
                d.getUnitPrice(),
                d.getQuantity(),
                d.getProduct().getImage()
            )
        ).collect(Collectors.toList());

        return new OrderDTO(
            order.getId(),
            order.getStatus(),
            order.getOrderDate(),
            order.getTotal(),
            details
        );
    }
}
