package com.app.HotelBooking.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.HotelBooking.dto.UsuarioDTO;
import com.app.HotelBooking.model.Usuario;
import com.app.HotelBooking.repository.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Usuario> listarUsuarios() {
        return repo.findAll();
    }

    @Override
    public List<UsuarioDTO> listarUsuarioDTO() {
        return repo.findAll().stream()
            .map(u -> new UsuarioDTO(
                u.getIdUsuario(),
                u.getNombreUsuario(),
                u.getRol()))
            .collect(Collectors.toList());
    }

    @Override
    public Usuario guardarUsuario(Usuario u) {
        // Al crear/editar, guardamos el hash
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return repo.save(u);
    }

@Override
public Optional<Usuario> login(String nombreUsuario, String password) {
  Optional<Usuario> opt = repo.findByNombreUsuario(nombreUsuario);
  if (opt.isPresent() &&
      passwordEncoder.matches(password, opt.get().getPassword())) {
    return opt;
  }
  return Optional.empty();
}



    // >>> Nuevo método requerido por SecurityConfig:
    @Override
    public Optional<Usuario> buscarPorNombre(String nombreUsuario) {
        return repo.findByNombreUsuario(nombreUsuario);
    }

    // Métodos CRUD que ya tenías:
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repo.findById(id);
    }

    @Override
    public boolean existePorId(Long id) {
        return repo.existsById(id);
    }

    @Override
    public void eliminarUsuario(Long id) {
        repo.deleteById(id);
    }
}
