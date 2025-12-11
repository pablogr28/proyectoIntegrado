package com.e_commerce.service;

import com.e_commerce.model.*;
import com.e_commerce.model.Order;
import com.e_commerce.model.Payment;
import com.e_commerce.repository.*;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    private final APIContext apiContext;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository; 

    public PaymentService(APIContext apiContext,
                          OrderDetailRepository orderDetailRepository,
                          OrderRepository orderRepository,
                          PaymentRepository paymentRepository,
                          UserRepository userRepository,
                          ProductRepository productRepository) { 
        this.apiContext = apiContext;
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository; 
    }

    // 🟢 Crear el pago PayPal
    public com.paypal.api.payments.Payment createPayPalPayment(Double total, Long userId, String cancelUrl, String successUrl)
            throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.format(java.util.Locale.US, "%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription("Pago de pedido del usuario #" + userId);
        transaction.setAmount(amount);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(List.of(transaction));

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }


    @Transactional
    public Payment confirmPayPalPayment(String paymentId, String payerId, Long userId)
            throws PayPalRESTException {

        System.out.println("✅ Iniciando confirmación de pago PayPal para userId=" + userId);

        // 1️⃣ Ejecutar el pago con PayPal
        com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        com.paypal.api.payments.Payment executed = payment.execute(apiContext, paymentExecution);
        System.out.println("Estado del pago ejecutado: " + executed.getState());

        if (!"approved".equalsIgnoreCase(executed.getState())) {
            throw new RuntimeException("El pago no fue aprobado. Estado: " + executed.getState());
        }

        // 2️⃣ Obtener usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3️⃣ Obtener carrito
        List<OrderDetail> cartItems = orderDetailRepository.findByUserIdAndOrderIsNull(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("El carrito está vacío, no se puede crear el pedido.");
        }

        // 4️⃣ Calcular total
        double total = cartItems.stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();

        // 5️⃣ Crear el pedido
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setTotal(total);
        order.setStatus("PAID");
        orderRepository.saveAndFlush(order);

        // 6️⃣ Asociar los OrderDetail al pedido
        for (OrderDetail item : cartItems) {
            item.setOrder(order);
            orderDetailRepository.save(item);
        }
        orderDetailRepository.flush();

        // 🟡 7️⃣ Reducir stock de los productos comprados
        for (OrderDetail item : cartItems) {
            Product product = item.getProduct();

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + product.getName());
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
        productRepository.flush();
        System.out.println("Stock actualizado correctamente.");

        // 8️⃣ Registrar el pago
        Payment newPayment = new Payment();
        newPayment.setOrder(order);
        newPayment.setPaymentMethod("PayPal");
        newPayment.setAmount(total);
        newPayment.setPaymentDate(LocalDate.now());
        newPayment.setPaymentStatus("APPROVED");
        paymentRepository.saveAndFlush(newPayment);

        // 9️⃣ Limpiar carrito
        List<OrderDetail> remainingCart = orderDetailRepository.findByUserIdAndOrderIsNull(userId);
        if (!remainingCart.isEmpty()) {
            orderDetailRepository.deleteAll(remainingCart);
            orderDetailRepository.flush();
        }

        return newPayment;
    }

}
