package com.e_commerce.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.e_commerce.model.CouponUser;
import com.e_commerce.model.User;
import com.e_commerce.model.Coupon;

public interface CouponUserRepository extends JpaRepository<CouponUser, Long> {
    Optional<CouponUser> findByUserAndCoupon(User user, Coupon coupon);
}
