package com.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_commerce.model.Order;
import com.e_commerce.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 🔹 Devuelve todas las órdenes de un usuario (historial de compras).
     */
    List<Order> findByUser(User user);

    /**
     * 🔹 Busca una orden específica por usuario e ID (útil para seguridad o validaciones).
     */
    Optional<Order> findByIdAndUser(Long id, User user);
    
    boolean existsByUserIdAndOrderDetailsProductId(Long userId, Long productId);


}
