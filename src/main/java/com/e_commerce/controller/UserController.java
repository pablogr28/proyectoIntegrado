package com.e_commerce.controller;

import com.e_commerce.model.Credential;
import com.e_commerce.model.ErrorResponse;
import com.e_commerce.model.User;
import com.e_commerce.model.UserDTO;
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

    // ---------- Registro ----------
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existente")
    })
    @PostMapping("/registrar")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO regDto) {
        try {
            UserDTO saved = userService.saveUser(regDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(), 400));
        }
    }

    // ---------- Login ----------
    @Operation(summary = "Login de usuario", description = "Autentica al usuario y devuelve un token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Credential credential) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credential.getUsername(), credential.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority())
                    .orElse("ROLE_USER");

            String token = TokenUtils.generateToken(userDetails.getUsername(), role);

            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales inválidas"));
        }
    }

    // ---------- Actualizar (usuario autenticado) ----------
    @Operation(summary = "Actualizar usuario", description = "Permite a un usuario autenticado modificar sus datos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
        @ApiResponse(responseCode = "403", description = "No puedes editar otro usuario"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
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

    // ---------- Actualizar (admin) ----------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar usuario (Admin)", description = "Permite a un administrador modificar datos de cualquier usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateUserAdmin(@PathVariable Long id, @RequestBody UserDTO regDto) {
        try {
            UserDTO updated = userService.updateUser(id, regDto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage(), 404));
        }
    }

    // ---------- Obtener todos ----------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve la lista de usuarios (solo admin)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ---------- Obtener por ID ----------
    @Operation(summary = "Obtener usuario por ID", description = "Devuelve la información de un usuario específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> optionalUser = userService.getUserById(id);
        return optionalUser.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Usuario con ID " + id + " no encontrado", 404)));
    }

    // ---------- Eliminar ----------
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario", description = "Permite a un administrador eliminar un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
