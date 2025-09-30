package com.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_commerce.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
