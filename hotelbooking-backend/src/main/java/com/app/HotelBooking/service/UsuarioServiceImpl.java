package com.app.HotelBooking.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.HotelBooking.dto.UsuarioDTO;
import com.app.HotelBooking.model.Usuario;
import com.app.HotelBooking.repository.UsuarioRepository;
// UsuarioServiceImpl.java
// Implementación del servicio de Usuarios
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
// Nuevo método para listar DTOs
    // sin password, para que no se exponga en la API
    @Override
    public List<UsuarioDTO> listarUsuarioDTO() {
        return repo.findAll().stream()
            .map(u -> new UsuarioDTO(
                u.getIdUsuario(),
                u.getNombreUsuario(),
                u.getRol()))
            .collect(Collectors.toList());
    }
// Método para guardar un nuevo usuario
    // si el password no empieza con $2a$, lo rehasheamos
@Override
public Usuario guardarUsuario(Usuario u) {
    String pwd = u.getPassword();
    // Si pwd es null o vacío, no lo tocamos;
    // si ya empieza con el prefijo BCrypt, tampoco lo rehasheamos
    if (pwd != null && !pwd.isBlank() && !pwd.startsWith("$2a$")) {
        u.setPassword(passwordEncoder.encode(pwd));
    }
    return repo.save(u);
}

// Método para login    
@Override
public Optional<Usuario> login(String nombreUsuario, String password) {
  Optional<Usuario> opt = repo.findByNombreUsuario(nombreUsuario);
  if (opt.isPresent() &&
      passwordEncoder.matches(password, opt.get().getPassword())) {
    return opt;
  }
  return Optional.empty();
}



    // Método para buscar usuario por nombre
    // devuelve un Optional<Usuario> para manejar el caso de no encontrarlo
    @Override
    public Optional<Usuario> buscarPorNombre(String nombreUsuario) {
        return repo.findByNombreUsuario(nombreUsuario);
    }

    // Métodos CRUD 
    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return repo.findById(id);
    }
// Verifica si existe un usuario por su ID
    // devuelve true o false según corresponda
    @Override
    public boolean existePorId(Long id) {
        return repo.existsById(id);
    }
// Elimina un usuario por su ID
    // lanza una excepción si no existe
    @Override
    public void eliminarUsuario(Long id) {
        repo.deleteById(id);
    }
}
