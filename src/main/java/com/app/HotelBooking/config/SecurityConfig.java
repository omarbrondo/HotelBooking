package com.app.HotelBooking.config;

import java.util.List;

import com.app.HotelBooking.security.JwtFilter;
import com.app.HotelBooking.service.UsuarioService;
import com.app.HotelBooking.model.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Configura el AuthenticationManager para que use tu UsuarioService + PasswordEncoder.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
          .userDetailsService(username -> {
              // 1) Busco tu Usuario en BD
              Usuario u = usuarioService
                .buscarPorNombre(username)
                .orElseThrow(() ->
                  new UsernameNotFoundException("Usuario no encontrado: " + username)
                );
              // 2) Convierto a UserDetails con ROLE_ prefijo
              List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + u.getRol())
              );
              UserDetails userDetails = User.builder()
                .username(u.getNombreUsuario())
                .password(u.getPassword())
                .authorities(authorities)
                .build();
              return userDetails;
          })
          .passwordEncoder(passwordEncoder);

        return authBuilder.build();
    }

    /**
     * Cadena de filtros de seguridad: 
     * - CSRF desactivado  
     * - Frame-Options sameOrigin (para H2)  
     * - Rutas públicas (assets, Swagger, H2, login/registro)  
     * - REST Stateless + JWTFilter  
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          // 1) Deshabilito CSRF (necesario para H2 console y APIs REST)
          .csrf(csrf -> csrf.disable())

          // 2) Configuro quién puede acceder a qué
          .authorizeHttpRequests(auth -> auth
            // 2.1) Recursos estáticos y página principal
            .requestMatchers(
              "/", "/index.html",
              "/js/**", "/css/**", "/img/**", "/favicon.ico"
            ).permitAll()

            // 2.2) Swagger / OpenAPI
            .requestMatchers(
              "/swagger-ui/**",
              "/swagger-ui.html",
              "/v3/api-docs/**",
              "/webjars/**"
            ).permitAll()

            // 2.3) H2 console
            .requestMatchers("/h2-console/**").permitAll()

            // 2.4) Login y registro (sin token)
            .requestMatchers("/usuario/login", "/usuario").permitAll()

            // 2.5) Cualquier otra petición requiere JWT
            .anyRequest().authenticated()
          )

          // 3) Permito iframes desde el mismo origen (para H2 console)
          .headers(headers -> headers
            .frameOptions(frame -> frame.sameOrigin())
          )

          // 4) Stateless session + nuestro filtro JWT
          .sessionManagement(sm ->
            sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
          .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
