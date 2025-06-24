// src/main/java/com/app/HotelBooking/repository/UsuarioRepository.java
package com.app.HotelBooking.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.HotelBooking.model.Usuario;
// UsuarioRepository.java // Repositorio para la entidad Usuario // permite operaciones CRUD y consultas personalizadas
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
  Optional<Usuario> findByNombreUsuario(String nombreUsuario);
}

