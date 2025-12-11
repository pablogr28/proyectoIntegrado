package com.e_commerce.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.e_commerce.model.Coupon;
import com.e_commerce.model.CouponUser;
import com.e_commerce.model.Order;
import com.e_commerce.model.User;
import com.e_commerce.repository.CouponRepository;
import com.e_commerce.repository.CouponUserRepository;
import com.e_commerce.repository.OrderRepository;
import com.e_commerce.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUserRepository couponUserRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public CouponService(
            CouponRepository couponRepository,
            CouponUserRepository couponUserRepository,
            UserRepository userRepository,
            OrderRepository orderRepository) {
        this.couponRepository = couponRepository;
        this.couponUserRepository = couponUserRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Valida un cupón sin necesidad de usuario ni pedido (para el carrito)
     */
    public Coupon validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado."));

        if (!Boolean.TRUE.equals(coupon.getActive())) {
            throw new RuntimeException("Este cupón está inactivo.");
        }

        LocalDate today = LocalDate.now();
        if ((coupon.getStartDate() != null && today.isBefore(coupon.getStartDate())) ||
            (coupon.getEndDate() != null && today.isAfter(coupon.getEndDate()))) {
            throw new RuntimeException("El cupón no está disponible en esta fecha.");
        }

        return coupon;
    }

    /**
     * Aplica un cupón a un pedido existente (cuando el pedido ya se ha creado)
     */
    @Transactional
    public Coupon validateAndApplyCoupon(String code, Long userId, Long orderId) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Cupón no encontrado."));

        if (!Boolean.TRUE.equals(coupon.getActive())) {
            throw new RuntimeException("Este cupón está inactivo.");
        }

        LocalDate today = LocalDate.now();
        if ((coupon.getStartDate() != null && today.isBefore(coupon.getStartDate())) ||
            (coupon.getEndDate() != null && today.isAfter(coupon.getEndDate()))) {
            throw new RuntimeException("El cupón no está disponible en esta fecha.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado."));

        Optional<CouponUser> existingUse = couponUserRepository.findByUserAndCoupon(user, coupon);
        if (existingUse.isPresent()) {
            throw new RuntimeException("Ya has usado este cupón.");
        }

        CouponUser couponUser = new CouponUser();
        couponUser.setCoupon(coupon);
        couponUser.setUser(user);
        couponUser.setOrder(order);
        couponUserRepository.save(couponUser);

        return coupon;
    }
}
