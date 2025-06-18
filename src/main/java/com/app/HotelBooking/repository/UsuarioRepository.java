// src/main/java/com/app/HotelBooking/repository/UsuarioRepository.java
package com.app.HotelBooking.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.HotelBooking.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreUsuarioAndPassword(String nombreUsuario, String password);
}
