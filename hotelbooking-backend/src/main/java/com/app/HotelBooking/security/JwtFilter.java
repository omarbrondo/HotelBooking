package com.app.HotelBooking.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.app.HotelBooking.util.JwtUtil;


@Component
public class JwtFilter extends OncePerRequestFilter {
  @Autowired private JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(
      @SuppressWarnings("null") HttpServletRequest req,
      @SuppressWarnings("null") HttpServletResponse res,
      @SuppressWarnings("null") FilterChain chain) throws ServletException, IOException {
    String auth = req.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      try {
        Claims claims = jwtUtil.validarToken(auth.substring(7));
        String user = claims.getSubject();
        String rol  = claims.get("rol", String.class);
        var authToken = new UsernamePasswordAuthenticationToken(
          user, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      } catch (JwtException e) {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        return;
      }
    }
    chain.doFilter(req, res);
  }
}
