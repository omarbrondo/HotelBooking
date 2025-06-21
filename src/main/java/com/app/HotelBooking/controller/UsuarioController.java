package com.app.HotelBooking.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.app.HotelBooking.dto.UsuarioDTO;
import com.app.HotelBooking.model.Usuario;
import com.app.HotelBooking.service.UsuarioService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // 1) Listar todos
    @GetMapping
    public List<Usuario> listar() {
        return service.listarUsuarios();
    }

    // 2) Listar DTOs (sin password)
    @GetMapping("/dto")
    public List<UsuarioDTO> listarDTO() {
        return service.listarUsuarioDTO();
    }

    // 3) Obtener uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                      .map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // 4) Crear nuevo usuario
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario u) {
        Usuario saved = service.guardarUsuario(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 5) Actualizar usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(
        @PathVariable Long id,
        @RequestBody Usuario datos
    ) {
        return service.buscarPorId(id)
            .map(existente -> {
                existente.setNombreUsuario(datos.getNombreUsuario());
                existente.setPassword(datos.getPassword());
                existente.setRol(datos.getRol());
                Usuario actualizado = service.guardarUsuario(existente);
                return ResponseEntity.ok(actualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // 6) Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        if (!service.existePorId(id)) {
            return ResponseEntity.notFound().build();
        }
        service.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // 7) Login (ya lo tenés)
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody Usuario cred) {
        Optional<Usuario> opt = service.login(
            cred.getNombreUsuario(),
            cred.getPassword()
        );
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario u = opt.get();
        UsuarioDTO dto = new UsuarioDTO(
          u.getIdUsuario(), 
          u.getNombreUsuario(), 
          u.getRol()
        );
        return ResponseEntity.ok(dto);
    }
}
