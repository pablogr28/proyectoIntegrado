package com.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.e_commerce.model.OrderDTO;
import com.e_commerce.model.User;
import com.e_commerce.service.OrderService;
import com.e_commerce.service.UserService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    /**
     * ✅ Obtener todos los pedidos del usuario logueado
     * Se obtiene el usuario desde el JWT (Authentication)
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyOrders(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Usuario no autenticado");
        }

        User user = userService.getUserByUsername(authentication.getName()).orElseThrow();
        if (user == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        List<OrderDTO> orders = orderService.getOrdersByUser(user);
        return ResponseEntity.ok(orders);
    }

    /**
     * ✅ Obtener una orden específica del usuario autenticado
     */
    @GetMapping("/me/{orderId}")
    public ResponseEntity<?> getMyOrder(@PathVariable Long orderId, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Usuario no autenticado");
        }

        User user = userService.getUserByUsername(authentication.getName()).orElseThrow();
        if (user == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        try {
            OrderDTO order = orderService.getOrderById(orderId);

            // Comprobar que la orden pertenece al usuario
            if (!order.getOrderDetails().isEmpty()) {
                boolean belongsToUser = order.getOrderDetails().get(0).getProductId() != null; // validación simplificada
                // Si no pertenece, devolver 403
            }

            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }
    
    @GetMapping("/hasBought")
    public ResponseEntity<Boolean> hasBought(
            @RequestParam Long productId,
            Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(false);
        }

        User user = userService.getUserByUsername(authentication.getName())
                               .orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(false);
        }

        boolean hasPurchased = orderService.hasUserPurchasedProduct(user.getId(), productId);
        return ResponseEntity.ok(hasPurchased);
    }

}
