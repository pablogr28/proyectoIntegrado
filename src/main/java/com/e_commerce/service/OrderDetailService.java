package com.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce.model.OrderDetail;
import com.e_commerce.repository.OrderDetailRepository;

@Service
public class OrderDetailService {
    
    private final OrderDetailRepository orderDetailRepository;
    
    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }
    
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    public OrderDetail getOrderDetailById(Long id) {
        return orderDetailRepository.findById(id).orElse(null);
    }

    public OrderDetail saveOrderDetail(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }
}

