package com.app.HotelBooking.service;

import com.app.HotelBooking.model.Habitacion;
import java.util.List;

public interface HabitacionService {
    List<Habitacion> obtenerHabitacionesLibres();
}
