package com.app.HotelBooking.service;

import com.app.HotelBooking.exception.ReservaException;
import com.app.HotelBooking.model.Habitacion;
import com.app.HotelBooking.model.Reserva;
import com.app.HotelBooking.repository.HabitacionRepository;
import com.app.HotelBooking.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository, HabitacionRepository habitacionRepository) {
        this.reservaRepository = reservaRepository;
        this.habitacionRepository = habitacionRepository;
    }

    @Override
    public Reserva obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaException("Reserva no encontrada"));
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
    
    @Override
    public Reserva actualizarReserva(Long id, Reserva reservaActualizada) {
        // Buscamos la reserva existente, de lo contrario se lanza una excepción
        Reserva reservaExistente = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaException("Reserva no encontrada"));
        
        // Actualizamos los campos que se desean modificar
        reservaExistente.setNombre(reservaActualizada.getNombre());
        reservaExistente.setApellido(reservaActualizada.getApellido());
        reservaExistente.setDni(reservaActualizada.getDni());
        reservaExistente.setFechaDesde(reservaActualizada.getFechaDesde());
        reservaExistente.setFechaHasta(reservaActualizada.getFechaHasta());
        
        // Guardamos la reserva actualizada en el repositorio
        return reservaRepository.save(reservaExistente);
    }
}
