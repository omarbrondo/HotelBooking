// src/main/java/com/app/HotelBooking/service/UsuarioService.java
package com.app.HotelBooking.service;

import java.util.List;
import java.util.Optional;
import com.app.HotelBooking.dto.UsuarioDTO;
import com.app.HotelBooking.model.Usuario;

public interface UsuarioService {
    List<Usuario> listarUsuarios();
    List<UsuarioDTO> listarUsuarioDTO();
    Usuario guardarUsuario(Usuario u);
    Optional<Usuario> login(String nombreUsuario, String password);

    // <<< NUEVOS MÉTODOS >>>
    Optional<Usuario> buscarPorId(Long id);
    boolean existePorId(Long id);
    void eliminarUsuario(Long id);
}
