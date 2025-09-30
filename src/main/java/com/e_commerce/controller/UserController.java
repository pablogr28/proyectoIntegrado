package com.e_commerce.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.e_commerce.model.User;
import com.e_commerce.model.UserDTO;
import com.e_commerce.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 404,
                    "error", "Not Found",
                    "message", "Usuario con ID " + id + " no encontrado."
                )
            );
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDTO userDto) {
        // Validar si ya existe un usuario con el mismo email o username
        if (userService.existsByEmail(userDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("message", "Ya existe un usuario con el correo " + userDto.getEmail())
            );
        }
        if (userService.existsByUsername(userDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("message", "Ya existe un usuario con el nombre de usuario " + userDto.getUsername())
            );
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword()); // En producción, recuerda hashear la contraseña
        user.setGender(userDto.getGender());
        user.setAddress(userDto.getAddress());
        user.setRole("USER");
        user.setStatus("ACTIVE");

        User saved = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody @Valid UserDTO userDto) {
        User existing = userService.getUserById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 404,
                    "error", "Not Found",
                    "message", "No se encontró el usuario con ID " + id
                )
            );
        }

        // Validar si el nuevo email o username ya están en uso por otro usuario
        if (!existing.getEmail().equals(userDto.getEmail()) && userService.existsByEmail(userDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("message", "Ya existe un usuario con el correo " + userDto.getEmail())
            );
        }
        if (!existing.getUsername().equals(userDto.getUsername()) && userService.existsByUsername(userDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("message", "Ya existe un usuario con el nombre de usuario " + userDto.getUsername())
            );
        }

        existing.setEmail(userDto.getEmail());
        existing.setName(userDto.getName());
        existing.setUsername(userDto.getUsername());
        existing.setPassword(userDto.getPassword()); // Recordar hashear
        existing.setGender(userDto.getGender());
        existing.setAddress(userDto.getAddress());

        User updated = userService.saveUser(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User existing = userService.getUserById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 404,
                    "error", "Not Found",
                    "message", "No se encontró el usuario con ID " + id
                )
            );
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
