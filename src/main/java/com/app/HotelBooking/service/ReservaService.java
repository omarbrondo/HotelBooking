package com.app.HotelBooking.service;

import com.app.HotelBooking.model.Reserva;

public interface ReservaService {
    Reserva crearReserva(Reserva reserva, Long idHabitacion);
}
