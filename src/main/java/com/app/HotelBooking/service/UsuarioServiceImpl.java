// src/main/java/com/app/HotelBooking/service/UsuarioServiceImpl.java
package com.app.HotelBooking.service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.HotelBooking.dto.UsuarioDTO;
import com.app.HotelBooking.model.Usuario;
import com.app.HotelBooking.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    @Override
    public List<Usuario> listarUsuarios() {
        return repo.findAll();
    }

    @Override
    public List<UsuarioDTO> listarUsuarioDTO() {
        return repo.findAll().stream()
            .map(u -> new UsuarioDTO(u.getIdUsuario(),
                                     u.getNombreUsuario(),
                                     u.getRol()))
            .collect(Collectors.toList());
    }

    @Override
    public Usuario guardarUsuario(Usuario u) {
        return repo.save(u);
    }

    @Override
    public Optional<Usuario> login(String nombreUsuario, String password) {
        return repo.findByNombreUsuarioAndPassword(nombreUsuario, password);
    }
}
