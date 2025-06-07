package com.app.HotelBooking.service;

import com.app.HotelBooking.exception.ReservaException;
import com.app.HotelBooking.model.Habitacion;
import com.app.HotelBooking.model.Reserva;
import com.app.HotelBooking.repository.HabitacionRepository;
import com.app.HotelBooking.repository.ReservaRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository, HabitacionRepository habitacionRepository) {
        this.reservaRepository = reservaRepository;
        this.habitacionRepository = habitacionRepository;
    }

    @Override
    public Reserva crearReserva(Reserva reserva, Long idHabitacion) {
        // Buscar la habitación por su id
        Habitacion habitacion = habitacionRepository.findById(idHabitacion)
                .orElseThrow(() -> new ReservaException("Habitación no encontrada"));

        // Verificar que la habitación esté libre
        if (!"libre".equalsIgnoreCase(habitacion.getEstado())) {
            throw new ReservaException("La habitación está ocupada");
        }

        // Opcional: Validación de reservas solapadas (comentario de ejemplo)

        // Asocia la reserva con la habitación y actualiza el estado
        habitacion.setReserva(reserva);
        habitacion.setEstado("ocupado");

        // Guarda la reserva y actualiza la habitación
        Reserva reservaGuardada = reservaRepository.save(reserva);
        habitacionRepository.save(habitacion);

        return reservaGuardada;
    }

    // Nuevo método para actualizar la reserva
    @Override
    public Reserva actualizarReserva(Long id, Reserva reservaActualizada) {
        Reserva reservaExistente = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaException("Reserva no encontrada"));

        // Actualiza los campos que se permiten modificar
        reservaExistente.setNombre(reservaActualizada.getNombre());
        reservaExistente.setApellido(reservaActualizada.getApellido());
        reservaExistente.setDni(reservaActualizada.getDni());
        reservaExistente.setFechaDesde(reservaActualizada.getFechaDesde());
        reservaExistente.setFechaHasta(reservaActualizada.getFechaHasta());

        // Si en el futuro deseas permitir actualizar otros campos o relaciones,
        // puedes hacerlo aquí.

        return reservaRepository.save(reservaExistente);
    }
}
