package com.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_commerce.model.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
