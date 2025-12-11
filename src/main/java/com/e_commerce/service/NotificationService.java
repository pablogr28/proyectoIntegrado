package com.e_commerce.service;

import com.e_commerce.model.Notification;
import com.e_commerce.model.Product;
import com.e_commerce.model.User;
import com.e_commerce.repository.NotificationRepository;
import com.e_commerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    /** Crear la notificación si el producto está agotado */
    public Notification createNotification(Long userId, Product product) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (product.getStock() > 0) {
            throw new RuntimeException("El producto ya está disponible");
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setProduct(product);
        notification.setSent(false);
        return notificationRepository.save(notification);
    }

    /** Enviar notificaciones cuando el producto vuelva a estar disponible */
    public void notifyUsers(Product product) {
        if (product.getStock() <= 0) return; 

        List<Notification> pending = notificationRepository.findByProductAndSentFalse(product);
        for (Notification n : pending) {
            sendProductAvailableEmail(n.getUser(), product); 
            n.setSent(true);
            notificationRepository.save(n);
        }
    }

    /** Método para enviar correo */
    private void sendProductAvailableEmail(User user, Product product) {
        String subject = "¡Producto disponible: " + product.getName() + "!";
        String productUrl = "http://localhost:4200/products/" + product.getId(); 

        String message = "¡Hola " + user.getUsername() + "!\n\n" +
                "El producto que te interesa, \"" + product.getName() + "\", ya está disponible para comprar.\n" +
                "Puedes verlo y adquirirlo aquí:\n" + productUrl + "\n\n" +
                "¡No te lo pierdas!";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}
