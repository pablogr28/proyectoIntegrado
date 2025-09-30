package com.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_commerce.model.OrderDetail;


public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

}
