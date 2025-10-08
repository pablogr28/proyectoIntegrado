package com.e_commerce.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.e_commerce.model.User;
import com.e_commerce.model.UserDTO;
import com.e_commerce.repository.UserRepository;



@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Listar todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Obtener usuario por ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Registrar usuario nuevo
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
        user.setStatus("active");
        user.setRegistrationDate(LocalDate.now());

        User saved = userRepository.save(user);
        return entityToDto(saved);
    }

    // Actualizar usuario (sin permitir cambiar el rol)
    public UserDTO updateUser(Long id, UserDTO userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setUsername(userDto.getUsername());

        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // NO se actualiza el role desde aquí

        User updated = userRepository.save(user);
        return entityToDto(updated);
    }

    // Actualizar datos completos (incluyendo rol) — para Admin
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

    // Borrar usuario
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Convertir User a UserDTO
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

    // Para autenticación JWT
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),   // correcto: username
                user.getPassword(),   // correcto: password
                List.of(new SimpleGrantedAuthority(user.getRole())) // correcto: authorities
        );
    }
}