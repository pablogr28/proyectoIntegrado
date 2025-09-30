package com.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_commerce.model.CouponUser;

public interface CouponUserRepository extends JpaRepository<CouponUser, Long> {

}
