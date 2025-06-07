package com.app.HotelBooking.controller;

import com.app.HotelBooking.model.Habitacion;
import com.app.HotelBooking.repository.HabitacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    private final HabitacionRepository habitacionRepository;

    @Autowired
    public HabitacionController(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    @GetMapping("/ocupadas")
    public ResponseEntity<List<Habitacion>> getHabitacionesOcupadas() {
        List<Habitacion> ocupadas = habitacionRepository.findByEstado("ocupado");
        return ResponseEntity.ok(ocupadas);
    }
}
