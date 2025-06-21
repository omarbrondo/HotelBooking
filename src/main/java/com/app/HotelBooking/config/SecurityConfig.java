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
              // 2) Convierto a UserDetails
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

 @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  http
    .csrf(csrf -> csrf.disable())
.authorizeHttpRequests(auth -> auth

        // 1) Permitimos tus vistas y assets de la app
        .requestMatchers(
          "/", "/index.html",
          "/js/**", "/css/**", "/img/**", "/favicon.ico"
        ).permitAll()

        // 2) Permitimos Swagger UI y OpenAPI
        .requestMatchers(
          "/swagger-ui/**",
          "/swagger-ui.html",
          "/v3/api-docs/**",
          "/webjars/**"     // si usas webjars de Swagger
        ).permitAll()

      // 2) Login/registro
      .requestMatchers("/usuario/login", "/usuario").permitAll()

      // 3) El resto con JWT
      .anyRequest().authenticated()
    )
    .sessionManagement(sm ->
      sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    )
    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

  return http.build();
}
}