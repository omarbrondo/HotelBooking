package com.app.HotelBooking.service;

import com.app.HotelBooking.model.Reserva;

public interface ReservaService {
    Reserva crearReserva(Reserva reserva, Long idHabitacion);

    Reserva obtenerReservaPorId(Long id);

    
    // Nuevo método para actualizar la reserva
    Reserva actualizarReserva(Long id, Reserva reservaActualizada);
}
