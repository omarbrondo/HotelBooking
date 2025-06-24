package com.app.HotelBooking.service;

import com.app.HotelBooking.model.Habitacion;
import com.app.HotelBooking.repository.HabitacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
// HabitacionServiceImpl.java
// Implementación del servicio de Habitaciones
// maneja la lógica de negocio para obtener habitaciones libres y ocupadas
@Service
public class HabitacionServiceImpl implements HabitacionService {

    private final HabitacionRepository habitacionRepository;

    public HabitacionServiceImpl(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }
// Implementación de los métodos definidos en HabitacionService
    @Override
    public List<Habitacion> obtenerHabitacionesLibres() {
        return habitacionRepository.findByEstado("libre");
    }
// Método para obtener habitaciones ocupadas
    @Override
    public List<Habitacion> obtenerHabitacionesOcupadas() {
        return habitacionRepository.findByEstado("ocupado");
    }
}
