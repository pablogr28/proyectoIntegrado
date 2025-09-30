package com.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce.model.CouponUser;
import com.e_commerce.repository.CouponUserRepository;

@Service
public class CouponUserService {
    
    private final CouponUserRepository couponUserRepository;
    
    @Autowired
    public CouponUserService(CouponUserRepository couponUserRepository) {
        this.couponUserRepository = couponUserRepository;
    }
    
    public List<CouponUser> getAllCouponUsers() {
        return couponUserRepository.findAll();
    }

    public CouponUser getCouponUserById(Long id) {
        return couponUserRepository.findById(id).orElse(null);
    }

    public CouponUser saveCouponUser(CouponUser couponUser) {
        return couponUserRepository.save(couponUser);
    }

    public void deleteCouponUser(Long id) {
        couponUserRepository.deleteById(id);
    }
}

