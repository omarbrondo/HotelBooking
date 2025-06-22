package com.app.HotelBooking.controller;

import com.app.HotelBooking.model.Habitacion;
import com.app.HotelBooking.service.HabitacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    private final HabitacionService habitacionService;

    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    // GET /api/habitaciones/libres
    @GetMapping("/libres")
    public ResponseEntity<List<Habitacion>> getHabitacionesLibres() {
        List<Habitacion> libres = habitacionService.obtenerHabitacionesLibres();
        return ResponseEntity.ok(libres);
    }

    // GET /api/habitaciones/ocupadas
    @GetMapping("/ocupadas")
    public ResponseEntity<List<Habitacion>> getHabitacionesOcupadas() {
        List<Habitacion> ocupadas = habitacionService.obtenerHabitacionesOcupadas();
        return ResponseEntity.ok(ocupadas);
    }
}
