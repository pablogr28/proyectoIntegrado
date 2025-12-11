package com.e_commerce.controller;

import com.e_commerce.model.Payment;
import com.e_commerce.service.PaymentService;
import com.paypal.api.payments.Links;
import com.paypal.base.rest.PayPalRESTException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 🟢 Crear el pago PayPal
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestParam Double total, @RequestParam Long userId) {
        try {
            String cancelUrl = frontendUrl + "/payment-cancel";
            String successUrl = frontendUrl + "/payment-success?userId=" + userId;

            // 🔹 Aquí usamos el nombre completo para evitar conflicto con tu entidad Payment
            com.paypal.api.payments.Payment payment =
                    paymentService.createPayPalPayment(total, userId, cancelUrl, successUrl);

            for (Links link : payment.getLinks()) {
                if ("approval_url".equalsIgnoreCase(link.getRel())) {
                    return ResponseEntity.ok(link.getHref());
                }
            }

            return ResponseEntity.badRequest().body("No se encontró URL de aprobación de PayPal");

        } catch (PayPalRESTException e) {
            return ResponseEntity.badRequest().body("Error al crear el pago: " + e.getMessage());
        }
    }

    // 🟢 Confirmar pago (éxito)
    @GetMapping("/success")
    public ResponseEntity<?> confirmPayment(@RequestParam String paymentId,
                                            @RequestParam String PayerID,
                                            @RequestParam Long userId) {
        try {
            Payment savedPayment = paymentService.confirmPayPalPayment(paymentId, PayerID, userId);
            return ResponseEntity.ok(savedPayment);
        } catch (PayPalRESTException e) {
            return ResponseEntity.badRequest().body("Error al confirmar el pago: " + e.getMessage());
        }
    }
}
