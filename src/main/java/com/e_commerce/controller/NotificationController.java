package com.e_commerce.controller;

import com.e_commerce.model.Product;
import com.e_commerce.model.User;
import com.e_commerce.service.NotificationService;
import com.e_commerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProductService productService;

    /** 🔹 Endpoint para que un usuario se suscriba a notificaciones de un producto agotado */
    @PostMapping("/products/{productId}/notify-me")
    public ResponseEntity<String> notifyMe(
            @PathVariable Long productId,
            @AuthenticationPrincipal User currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(401).body("Debes iniciar sesión para solicitar notificaciones");
        }

        try {
            Product product = productService.getProductById(productId);
            notificationService.createNotification(currentUser.getId(), product);
            return ResponseEntity.ok("Te notificaremos cuando el producto vuelva a estar disponible");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
