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

        // Opcional: Validación avanzada de reservas solapadas.
        // Si en el futuro se permite tener varias reservas históricas o futuras para la misma habitación,
        // se podría agregar aquí una consulta que verifique si el rango [fechaDesde, fechaHasta]
        // se solapa con alguna reserva existente para esta habitación.
        //
        // Ejemplo (pseudocódigo):
        // List<Reserva> reservasExistentes = reservaRepository.findByHabitacionAndFechaOverlap(
        //     habitacion, reserva.getFechaDesde(), reserva.getFechaHasta());
        // if (!reservasExistentes.isEmpty()) {
        //     throw new ReservaException("El rango de fechas se solapa con una reserva existente");
        // }

        // Asocia la reserva con la habitación y actualiza el estado
        habitacion.setReserva(reserva);
        habitacion.setEstado("ocupado");

        // Guarda la reserva y actualiza la habitación
        Reserva reservaGuardada = reservaRepository.save(reserva);
        habitacionRepository.save(habitacion);

        return reservaGuardada;
    }
}
