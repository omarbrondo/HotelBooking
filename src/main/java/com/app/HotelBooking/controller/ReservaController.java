package com.app.HotelBooking.controller;

import com.app.HotelBooking.dto.ReservaDTO;
import com.app.HotelBooking.model.Habitacion;
import com.app.HotelBooking.model.Reserva;
import com.app.HotelBooking.service.HabitacionService;
import com.app.HotelBooking.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservaController {

    private final HabitacionService habitacionService;
    private final ReservaService reservaService;

    @Autowired
    public ReservaController(HabitacionService habitacionService, ReservaService reservaService) {
        this.habitacionService = habitacionService;
        this.reservaService = reservaService;
    }

    // Endpoint para obtener las habitaciones libres
    @GetMapping("/habitaciones/libres")
    public ResponseEntity<List<Habitacion>> getHabitacionesLibres() {
        List<Habitacion> habitacionesLibres = habitacionService.obtenerHabitacionesLibres();
        return ResponseEntity.ok(habitacionesLibres);
    }

    // Endpoint para actualizar una reserva existente
    @PutMapping("/reservas/{id}")
    public ResponseEntity<Reserva> actualizarReserva(@PathVariable Long id, @RequestBody Reserva reservaActualizada) {
        Reserva reservaActual = reservaService.actualizarReserva(id, reservaActualizada);
        return ResponseEntity.ok(reservaActual);
    }

    // Endpoint para registrar una nueva reserva
    @PostMapping("/reservas")
    public ResponseEntity<Reserva> crearReserva(@RequestBody ReservaDTO reservaDTO) {
        // Convertir el DTO a la entidad Reserva
        Reserva reserva = new Reserva();
        reserva.setNombre(reservaDTO.getNombre());
        reserva.setApellido(reservaDTO.getApellido());
        reserva.setDni(reservaDTO.getDni());
        reserva.setFechaDesde(reservaDTO.getFechaDesde());
        reserva.setFechaHasta(reservaDTO.getFechaHasta());

        Reserva reservaGuardada = reservaService.crearReserva(reserva, reservaDTO.getIdHabitacion());
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaGuardada);
    }
}
