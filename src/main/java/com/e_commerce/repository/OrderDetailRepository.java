package com.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_commerce.model.Order;
import com.e_commerce.model.OrderDetail;
import com.e_commerce.model.Product;
import com.e_commerce.model.User;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    // Carrito actual (sin pedido confirmado)
    List<OrderDetail> findByUserAndOrderIsNull(User user);

    Optional<OrderDetail> findByUserAndProductAndOrderIsNull(User user, Product product);

    List<OrderDetail> findByOrder(Order order);
    
    Optional<OrderDetail> findByUserAndProductNameAndOrderIsNull(User user, String productName);
    
    List<OrderDetail> findByUser(User user);
    
	List<OrderDetail> findByUserIdAndOrderIsNull(Long userId);

}
