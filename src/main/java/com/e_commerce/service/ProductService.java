package com.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce.model.Product;
import com.e_commerce.repository.ProductRepository;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product saveProduct(Product product) {
        boolean wasOutOfStock = false;
        if (product.getId() != null) {
            Product existing = productRepository.findById(product.getId()).orElse(null);
            if (existing != null) {
                wasOutOfStock = existing.getStock() <= 0;
            }
        }

        Product saved = productRepository.save(product);

        if (wasOutOfStock && saved.getStock() > 0) {
            notificationService.notifyUsers(saved);
        }

        return saved;
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public List<Product> findByNameContainingIgnoreCase(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

}
