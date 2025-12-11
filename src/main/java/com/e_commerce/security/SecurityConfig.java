package com.e_commerce.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final RequestFilter requestFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(UserDetailsService userDetailsService,
                          RequestFilter requestFilter,
                          CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.requestFilter = requestFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
            	    // Swagger
            	    .requestMatchers(
            	        "/swagger-ui/**",
            	        "/swagger-ui.html",
            	        "/v3/api-docs/**",
            	        "/swagger-resources/**",
            	        "/webjars/**"
            	    ).permitAll()

            	    // Login / Registro
            	    .requestMatchers("/users/registrar", "/users/login", "/users/verify",
            	                     "/users/forgot-password", "/users/reset-password").permitAll()

            	    // GET públicos
            	    .requestMatchers(HttpMethod.GET, "/categories/**", "/products/**", "/reviews/**")
            	    .permitAll()

            	    // Carrito público sin token
            	    .requestMatchers("/cart/user/**").permitAll()

            	    // Carrito con token
            	    .requestMatchers("/cart/me/**", "/cart/add/**").authenticated()

            	    // Solo administrador puede EDITAR o AÑADIR productos
            	    .requestMatchers(HttpMethod.PUT, "/products/update/**").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.POST, "/products/add").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.PUT, "/users/role").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.PUT, "/users/status").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.PATCH, "/products/**").hasRole("ADMIN")

            	    // Cupones público
            	    .requestMatchers("/api/coupons/**").permitAll()
            	    
            	    //Realizar pagos debes estar autenticado
            	    .requestMatchers("/api/payments/**").authenticated()
            	    
            	    .requestMatchers(HttpMethod.POST, "/reviews/user/**").authenticated()

            	    
            	    //Ver tus pedidos
            	    .requestMatchers("/orders/**").authenticated()
            	    
            	    // Agregar después de las reglas existentes:
            	    .requestMatchers("/notifications/**").authenticated()

            	    // DELETE users solo ADMIN
            	    .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

            	    // Resto necesita token
            	    .anyRequest().denyAll()
            	);

        // Filtro JWT
        http.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuración CORS global
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
