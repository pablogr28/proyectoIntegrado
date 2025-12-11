package com.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.e_commerce.model.OrderDetailDTO;
import com.e_commerce.model.Order;
import com.e_commerce.model.User;
import com.e_commerce.service.OrderDetailService;
import com.e_commerce.service.UserService;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @PostMapping("/user/{userId}/add/{productId}")
    public ResponseEntity<?> addToCart(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {

        User user = userService.findById(userId);
        if (user == null) return ResponseEntity.badRequest().body("Usuario no encontrado");

        OrderDetailDTO dto = orderDetailService.addProductToCart(user, productId, quantity);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) return ResponseEntity.badRequest().body("Usuario no encontrado");

        List<OrderDetailDTO> cartDTO = orderDetailService.getCartByUser(user);
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/user/{userId}/removeByName/{productName}")
    public ResponseEntity<?> removeFromCartByName(
            @PathVariable Long userId,
            @PathVariable String productName) {

        User user = userService.findById(userId);
        if (user == null) return ResponseEntity.badRequest().body("Usuario no encontrado");

        orderDetailService.removeFromCartByName(user, productName);
        return ResponseEntity.ok("Producto eliminado del carrito");
    }

    @DeleteMapping("/user/{userId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) return ResponseEntity.badRequest().body("Usuario no encontrado");

        orderDetailService.clearCart(user);
        return ResponseEntity.ok("Carrito vaciado");
    }

    @PostMapping("/user/{userId}/checkout")
    public ResponseEntity<?> checkout(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) return ResponseEntity.badRequest().body("Usuario no encontrado");

        Order order = orderDetailService.checkout(user);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/user/{userId}/updateByName/{productName}")
    public ResponseEntity<?> updateCartByName(
            @PathVariable Long userId,
            @PathVariable String productName,
            @RequestParam Integer quantity) {

        User user = userService.findById(userId);
        if (user == null) return ResponseEntity.badRequest().body("Usuario no encontrado");

        orderDetailService.updateCartByName(user, productName, quantity);
        return ResponseEntity.ok("Cantidad actualizada");
    }
}
