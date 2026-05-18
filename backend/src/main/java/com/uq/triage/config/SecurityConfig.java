package com.uq.triage.config;

import com.uq.triage.security.JwtFiltro;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

/**
 * Configuracion central de Spring Security con JWT (RF-13).
 * Cada endpoint tiene permisos de rol estrictamente definidos.
 * La sesion es STATELESS: no se usan cookies ni sesiones del servidor.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFiltro jwtFiltro;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints publicos
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                // Registro: cualquier usuario autenticado
                .requestMatchers(HttpMethod.POST, "/api/v1/solicitudes")
                    .hasAnyRole("ESTUDIANTE", "DOCENTE", "ADMINISTRATIVO")
                // Consulta: todos los roles autenticados
                .requestMatchers(HttpMethod.GET, "/api/v1/solicitudes/**")
                    .hasAnyRole("ESTUDIANTE", "DOCENTE", "ADMINISTRATIVO")
                // Operaciones de gestion: solo ADMINISTRATIVO
                .requestMatchers(HttpMethod.PATCH, "/api/v1/solicitudes/*/clasificar")
                    .hasRole("ADMINISTRATIVO")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/solicitudes/*/asignar-responsable")
                    .hasRole("ADMINISTRATIVO")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/solicitudes/*/atender")
                    .hasRole("ADMINISTRATIVO")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/solicitudes/*/cerrar")
                    .hasRole("ADMINISTRATIVO")
                // IA: solo ADMINISTRATIVO
                .requestMatchers("/api/v1/ia/**").hasRole("ADMINISTRATIVO")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg)
            throws Exception { return cfg.getAuthenticationManager(); }

    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:4200"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
