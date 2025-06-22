package com.app.HotelBooking.service;

import com.app.HotelBooking.model.Reserva;

public interface ReservaService {
    Reserva obtenerReservaPorId(Long id);
    Reserva crearReserva(Reserva reserva, Long idHabitacion);
    Reserva actualizarReserva(Long id, Reserva reservaActualizada);
    void eliminarReserva(Long id);  // <-- Agregar este método
}
