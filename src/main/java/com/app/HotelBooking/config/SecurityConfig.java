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
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        // → Rutas públicas:
        .requestMatchers(
          "/", "/index.html",
          "/js/**", "/css/**", "/img/**", "/favicon.ico",
          "/swagger-ui/**", "/v3/api-docs/**",
          "/h2-console/**",
          "/usuario/login", "/usuario"
        ).permitAll()

        // → Dashboard: permiso público
        .requestMatchers("/api/dashboard/**").permitAll()

        // → O, para que sólo ADMIN lo vea:
        // .requestMatchers("/api/dashboard/**")
        //   .hasRole("ADMINISTRADOR")

        // → El resto requiere autenticación
        .anyRequest().authenticated()
      )
      .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
      .sessionManagement(sm ->
        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}

}
