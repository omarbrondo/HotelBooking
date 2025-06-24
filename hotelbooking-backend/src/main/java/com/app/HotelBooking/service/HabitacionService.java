package com.app.HotelBooking.service;

import com.app.HotelBooking.model.Habitacion;
import java.util.List;
// HabitacionService.java// Interfaz para el servicio de Habitaciones // define métodos para obtener habitaciones libres y ocupadas

public interface HabitacionService {
    List<Habitacion> obtenerHabitacionesLibres();
    List<Habitacion> obtenerHabitacionesOcupadas();
}
