package com.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce.model.Coupon;
import com.e_commerce.repository.CouponRepository;

@Service
public class CouponService {
	
private final CouponRepository couponRepository;
	
	@Autowired
	public CouponService(CouponRepository couponRepository) {
		this.couponRepository=couponRepository;
	}
	
	public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id).orElse(null);
    }

    public Coupon saveCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public void deleteCategory(Long id) {
    	couponRepository.deleteById(id);
    }

}
