package com.e_commerce.controller;

import com.e_commerce.model.Credential;
import com.e_commerce.model.ErrorResponse;
import com.e_commerce.model.User;
import com.e_commerce.model.UserDTO;
import com.e_commerce.model.VerificationToken;
import com.e_commerce.repository.UserRepository;
import com.e_commerce.repository.VerificationTokenRepository;
import com.e_commerce.security.TokenUtils;
import com.e_commerce.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Controlador para la gestión de usuarios en e_commerce")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;
    
    

 // ---------------- REGISTRO ----------------
    @PostMapping("/registrar")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO regDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validación: nombre de usuario duplicado
            if (userRepository.existsByUsername(regDto.getUsername())) {
                response.put("success", false);
                response.put("message", "Ya existe un usuario con ese nombre de usuario.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Validación: correo electrónico duplicado
            if (userRepository.findByEmail(regDto.getEmail()) == null) {
                response.put("message", "Ya existe un usuario registrado con ese correo electrónico.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

         // Validación de contraseña mínima con complejidad
            String password = regDto.getPassword();
            if (password == null || password.isEmpty()) {
                response.put("success", false);
                response.put("message", "La contraseña no puede estar vacía.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

            if (!password.matches(pattern)) {
                response.put("success", false);
                response.put("message", "La contraseña debe tener al menos 8 caracteres, " +
                        "incluyendo una letra minúscula, una mayúscula, un número y un carácter especial (@$!%*?&).");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }


            // Guardar usuario
            UserDTO saved = userService.saveUser(regDto);

            // Respuesta exitosa
            response.put("success", true);
            response.put("message", "Usuario registrado correctamente. Revisa tu correo para verificar tu cuenta.");
            response.put("user", saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al registrar el usuario. Intenta nuevamente.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Credential credential) {
        Map<String, String> response = new HashMap<>();

        try {
            // Verificar que exista el usuario
            Optional<User> optionalUser = userService.getUserByUsername(credential.getUsername());
            if (optionalUser.isEmpty()) {
                response.put("error", "Usuario no encontrado.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            User user = optionalUser.get();

            // Verificar que el usuario esté activo/verificado
            if (!user.isEnabled()) {
                response.put("error", "Cuenta no verificada. Revisa tu correo para activarla.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Autenticación (usuario y contraseña)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credential.getUsername(), credential.getPassword()));

            // Generar token JWT
            String token = TokenUtils.generateToken(user);
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Contraseña incorrecta u otro error de autenticación
            response.put("error", "Contraseña incorrecta");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    // ---------------- ACTUALIZAR USUARIO ----------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO regDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> userOptional = userService.getUserByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        User currentUser = userOptional.get();
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes editar otro usuario");
        }

        try {
            UserDTO updated = userService.updateUser(id, regDto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage(), 404));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateUserAdmin(@PathVariable Long id, @RequestBody UserDTO regDto) {
        try {
            UserDTO updated = userService.updateUser(id, regDto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage(), 404));
        }
    }

    // ---------------- OBTENER / ELIMINAR ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
            .map(user -> {
                UserDTO dto = new UserDTO();
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setName(user.getName());
                dto.setRole(user.getRole());
                dto.setStatus(user.getStatus());
                dto.setRegistrationDate(user.getRegistrationDate());
                return dto;
            })
            .toList();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> optionalUser = userService.getUserById(id);
        return optionalUser.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Usuario con ID " + id + " no encontrado", 404)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

 // ---------------- VERIFICAR CUENTA ----------------
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Boolean>> verifyUser(@RequestParam("token") String token) {
        Map<String, Boolean> response = new HashMap<>();

        try {
            Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);
            if (optionalToken.isEmpty()) {
                response.put("verified", false);
                return ResponseEntity.badRequest().body(response);
            }

            VerificationToken verificationToken = optionalToken.get();
            User user = verificationToken.getUser();

            if (user == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                // Token expirado o inválido
                tokenRepository.delete(verificationToken);
                response.put("verified", false);
                return ResponseEntity.badRequest().body(response);
            }

            // Si el usuario aún no estaba activo, activarlo
            if (!user.isEnabled()) {
                user.setEnabled(true);
                user.setStatus("active");
                userRepository.save(user);
            }

            // Eliminar el token después de verificar
            tokenRepository.delete(verificationToken);

            response.put("verified", true);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("verified", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // ---------------- RECUPERAR CONTRASEÑA ----------------
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        try {
            userService.createPasswordResetToken(email);
            return ResponseEntity.ok(Map.of("message", "Correo de recuperación enviado"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), 400));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestParam("password") String password) {
        try {
            userService.resetPassword(token, password);
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), 400));
        }
    }
    
 // ---------------- CAMBIAR ROL ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/role")
    public ResponseEntity<?> changeUserRole(@RequestParam String email,
                                            @RequestParam boolean makeAdmin) {
        try {
            userService.changeUserRoleByEmail(email, makeAdmin);
            return ResponseEntity.ok(Map.of("message", "Rol actualizado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // ---------------- CAMBIAR STATUS ----------------
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/status")
    public ResponseEntity<?> changeUserStatus(@RequestParam String email,
                                              @RequestParam boolean block) {
        try {
            userService.changeUserStatusByEmail(email, block);
            return ResponseEntity.ok(Map.of("message", "Estado actualizado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
