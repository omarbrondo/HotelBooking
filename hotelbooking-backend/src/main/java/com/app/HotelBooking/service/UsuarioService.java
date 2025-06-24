// src/main/java/com/app/HotelBooking/service/UsuarioService.java
package com.app.HotelBooking.service;

import java.util.List;
import java.util.Optional;
import com.app.HotelBooking.dto.UsuarioDTO;
import com.app.HotelBooking.model.Usuario;
// Interfaz para el servicio de Usuarios
// define métodos para manejar usuarios, incluyendo login y operaciones CRUD
public interface UsuarioService {
    List<Usuario> listarUsuarios();
    List<UsuarioDTO> listarUsuarioDTO();
    Usuario guardarUsuario(Usuario u);
Optional<Usuario> login(String nombreUsuario, String password);

// Métodos CRUD 
    Optional<Usuario> buscarPorId(Long id);
Optional<Usuario> buscarPorNombre(String nombreUsuario);

    boolean existePorId(Long id);
    void eliminarUsuario(Long id);
}
