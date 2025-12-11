package com.e_commerce.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.e_commerce.model.PasswordResetToken;
import com.e_commerce.model.User;
import com.e_commerce.model.UserDTO;
import com.e_commerce.model.VerificationToken;
import com.e_commerce.repository.PasswordResetTokenRepository;
import com.e_commerce.repository.UserRepository;
import com.e_commerce.repository.VerificationTokenRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenRepository resetTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ------------------- LISTAR / OBTENER -------------------
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ------------------- REGISTRO -------------------
    public UserDTO saveUser(UserDTO userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre de usuario.");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setGender(userDto.getGender());
        user.setAddress(userDto.getAddress());
        user.setRole("ROLE_USER");
        user.setStatus("pending_verification");
        user.setEnabled(false);
        user.setRegistrationDate(LocalDate.now());

        User saved = userRepository.save(user);

        // Generar token de verificación
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
            token,
            saved,
            LocalDateTime.now().plusHours(24)
        );
        tokenRepository.save(verificationToken);

        // Enviar correo
        sendVerificationEmail(saved, token);

        return entityToDto(saved);
    }

    private void sendVerificationEmail(User user, String token) {
        String subject = "Verifica tu cuenta en RetroGol ⚽";
        String verificationUrl = "http://localhost:4200/verify?token=" + token;

        String message = "¡Hola " + user.getUsername() + "!\n\n" +
                "Gracias por registrarte en RetroGol.\n" +
                "Haz clic en el siguiente enlace para activar tu cuenta:\n" +
                verificationUrl + "\n\n" +
                "Este enlace expirará en 24 horas.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }

    // ------------------- ACTUALIZAR USUARIO -------------------
    public UserDTO updateUser(Long id, UserDTO userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setUsername(userDto.getUsername());
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        User updated = userRepository.save(user);
        return entityToDto(updated);
    }

    public UserDTO adminUpdateUser(Long id, UserDTO userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setUsername(userDto.getUsername());
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        User updated = userRepository.save(user);
        return entityToDto(updated);
    }

    // ------------------- ELIMINAR -------------------
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ------------------- CONVERTIR DTO -------------------
    private UserDTO entityToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setGender(user.getGender());
        dto.setAddress(user.getAddress());
        dto.setRegistrationDate(user.getRegistrationDate());
        return dto;
    }

    // ------------------- AUTENTICACIÓN -------------------
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("Cuenta no activada. Revisa tu correo para verificarla.");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }

    // ------------------- RECUPERACIÓN DE CONTRASEÑA -------------------

    public void createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con ese correo no encontrado"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(2));
        resetTokenRepository.save(resetToken);

        sendPasswordResetEmail(user, token);
    }

    private void sendPasswordResetEmail(User user, String token) {
        String subject = "Recupera tu contraseña - RetroGol";
        String resetUrl = "http://localhost:4200/reset-password?token=" + token;

        String message = "¡Hola " + user.getUsername() + "!\n\n" +
                "Recibimos una solicitud para cambiar tu contraseña.\n" +
                "Haz clic en el siguiente enlace para establecer una nueva contraseña:\n" +
                resetUrl + "\n\n" +
                "Este enlace expirará en 2 horas.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            resetTokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Token expirado");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetTokenRepository.delete(resetToken);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + email));
    }
   

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public void changeUserRoleByEmail(String email, boolean makeAdmin) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String oldRole = user.getRole();
        user.setRole(makeAdmin ? "ROLE_ADMIN" : "ROLE_USER");

        userRepository.save(user);

        sendRoleChangeEmail(user, makeAdmin, oldRole);
    }


    private void sendRoleChangeEmail(User user, boolean makeAdmin, String oldRole) {
        String subject = "Cambio de rol en RetroGol";
        String message;

        if (makeAdmin && !"ROLE_ADMIN".equals(oldRole)) {
            message = "¡Hola " + user.getUsername() + "! Se te ha asignado el rol de administrador.";
        } else if (!makeAdmin && "ROLE_ADMIN".equals(oldRole)) {
            message = "Hola " + user.getUsername() + ". Se ha eliminado tu rol de administrador.";
        } else {
            return;
        }

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
    
    public void changeUserStatusByEmail(String email, boolean block) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setStatus(block ? "blocked" : "active");

        userRepository.save(user);

        sendBlockStatusEmail(user, block);
    }
    
    private void sendBlockStatusEmail(User user, boolean block) {
        String subject = "Estado de cuenta actualizado";
        String message = block
                ? "Tu cuenta ha sido bloqueada por un administrador."
                : "Tu cuenta ha sido reactivada. Ya puedes volver a acceder.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }




}
