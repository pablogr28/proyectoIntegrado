package com.e_commerce.repository;

import com.e_commerce.model.Notification;
import com.e_commerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByProductAndSentFalse(Product product);
}
