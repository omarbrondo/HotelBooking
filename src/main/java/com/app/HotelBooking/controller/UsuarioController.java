// src/main/java/com/app/HotelBooking/controller/UsuarioController.java
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

    @GetMapping
    public List<Usuario> listar() {
        return service.listarUsuarios();
    }

    @GetMapping("/dto")
    public List<UsuarioDTO> listarDTO() {
        return service.listarUsuarioDTO();
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario u) {
        Usuario saved = service.guardarUsuario(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody Usuario cred) {
        Optional<Usuario> opt = service.login(cred.getNombreUsuario(), cred.getPassword());
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario u = opt.get();
        UsuarioDTO dto = new UsuarioDTO(u.getIdUsuario(), u.getNombreUsuario(), u.getRol());
        return ResponseEntity.ok(dto);
    }
}
