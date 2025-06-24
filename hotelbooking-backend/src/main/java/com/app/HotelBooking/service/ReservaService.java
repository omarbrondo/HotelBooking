package com.app.HotelBooking.service;

import com.app.HotelBooking.model.Reserva;
// ReservaService.java // Interfaz para el servicio de Reservas // define métodos para manejar reservas
public interface ReservaService {
    Reserva obtenerReservaPorId(Long id);
    Reserva crearReserva(Reserva reserva, Long idHabitacion);
    Reserva actualizarReserva(Long id, Reserva reservaActualizada);
    void eliminarReserva(Long id);  // <-- Agregar este método
}
