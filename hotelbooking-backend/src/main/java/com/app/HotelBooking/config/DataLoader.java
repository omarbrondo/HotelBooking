package com.app.HotelBooking.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.app.HotelBooking.model.Usuario;
import com.app.HotelBooking.service.UsuarioService;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioService.listarUsuarios().isEmpty()) {
            usuarioService.guardarUsuario(
                new Usuario("admin",    "admin",    "ADMINISTRADOR")
            );
            usuarioService.guardarUsuario(
                new Usuario("empleado", "empleado", "EMPLEADO")
            );
        }
    }
}
