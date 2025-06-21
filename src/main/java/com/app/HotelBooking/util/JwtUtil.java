package com.app.HotelBooking.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
  @Value("${jwt.secret}")
  private String secret;

  private final long EXP_MS = 4 * 60 * 60 * 1000; // 4 horas

  public String generarToken(String username, String rol) {
    return Jwts.builder()
      .setSubject(username)
      .claim("rol", rol)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + EXP_MS))
      .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
      .compact();
  }

  public Claims validarToken(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(secret.getBytes())
      .build()
      .parseClaimsJws(token)
      .getBody();
  }
}
