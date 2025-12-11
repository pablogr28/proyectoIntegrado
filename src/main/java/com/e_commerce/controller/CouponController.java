package com.e_commerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.e_commerce.model.Coupon;
import com.e_commerce.service.CouponService;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    /**
     * Valida cupón sin necesidad de pedido (para carrito)
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateCoupon(@RequestParam String code) {
        try {
            Coupon coupon = couponService.validateCoupon(code);
            return ResponseEntity.ok(coupon);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Aplica cupón a un pedido existente (cuando ya hay orderId)
     */
    @PostMapping("/apply")
    public ResponseEntity<?> applyCoupon(
            @RequestParam String code,
            @RequestParam Long userId,
            @RequestParam Long orderId) {
        try {
            Coupon coupon = couponService.validateAndApplyCoupon(code, userId, orderId);
            return ResponseEntity.ok(coupon);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
