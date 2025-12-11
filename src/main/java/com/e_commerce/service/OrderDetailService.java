package com.e_commerce.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce.model.OrderDetailDTO;
import com.e_commerce.model.Order;
import com.e_commerce.model.OrderDetail;
import com.e_commerce.model.Product;
import com.e_commerce.model.User;
import com.e_commerce.repository.OrderDetailRepository;
import com.e_commerce.repository.OrderRepository;
import com.e_commerce.repository.ProductRepository;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    // Mapper de entidad a DTO
    public OrderDetailDTO mapToDTO(OrderDetail detail) {
        return new OrderDetailDTO(
            detail.getId(),
            detail.getProduct().getId(),
            detail.getProductName(),
            detail.getUnitPrice(),
            detail.getQuantity(),
            detail.getProduct().getImage()
        );
    }

    // ✅ Añadir producto al carrito
    public OrderDetailDTO addProductToCart(User user, Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Optional<OrderDetail> existingDetailOpt = orderDetailRepository.findByUserAndProductAndOrderIsNull(user, product);

        OrderDetail detail;
        if (existingDetailOpt.isPresent()) {
            detail = existingDetailOpt.get();
            detail.setQuantity(detail.getQuantity() + quantity);
        } else {
            detail = new OrderDetail();
            detail.setUser(user);
            detail.setProduct(product);
            detail.setProductName(product.getName());
            detail.setQuantity(quantity);
            detail.setUnitPrice(product.getPrice());
        }

        OrderDetail savedDetail = orderDetailRepository.save(detail);
        return mapToDTO(savedDetail);
    }

    // ✅ Obtener carrito actual
    public List<OrderDetailDTO> getCartByUser(User user) {
        List<OrderDetail> cart = orderDetailRepository.findByUserAndOrderIsNull(user);
        return cart.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // ✅ Eliminar del carrito por nombre
    public void removeFromCartByName(User user, String productName) {
        Optional<OrderDetail> detail = orderDetailRepository.findByUserAndProductNameAndOrderIsNull(user, productName);
        detail.ifPresent(orderDetailRepository::delete);
    }

    // ✅ Vaciar carrito
    public void clearCart(User user) {
        List<OrderDetail> cart = orderDetailRepository.findByUserAndOrderIsNull(user);
        orderDetailRepository.deleteAll(cart);
    }
    
    // ✅ Checkout
    public Order checkout(User user) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        orderRepository.save(order);

        List<OrderDetail> cartItems = orderDetailRepository.findByUserAndOrderIsNull(user);
        List<OrderDetail> orderItems = new ArrayList<>();

        for (OrderDetail cartItem : cartItems) {
            OrderDetail od = new OrderDetail();
            od.setProduct(cartItem.getProduct());
            od.setQuantity(cartItem.getQuantity());
            od.setUnitPrice(cartItem.getUnitPrice());
            od.setOrder(order);
            orderItems.add(od);
        }

        orderDetailRepository.saveAll(orderItems);
        orderDetailRepository.deleteAll(cartItems);

        return order;
    }
    
    // ✅ Actualizar cantidad por nombre
    public void updateCartByName(User user, String productName, Integer quantity) {
        if (quantity <= 0) {
            removeFromCartByName(user, productName);
            return;
        }

        Optional<OrderDetail> detailOpt = orderDetailRepository.findByUserAndProductNameAndOrderIsNull(user, productName);

        if (detailOpt.isPresent()) {
            OrderDetail detail = detailOpt.get();
            detail.setQuantity(quantity);
            orderDetailRepository.save(detail);
        } else {
            throw new IllegalArgumentException("Producto no encontrado en el carrito");
        }
    }
}
