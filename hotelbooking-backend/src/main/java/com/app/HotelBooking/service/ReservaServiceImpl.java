package com.app.HotelBooking.service;

import com.app.HotelBooking.exception.ReservaException;
import com.app.HotelBooking.model.Habitacion;
import com.app.HotelBooking.model.Reserva;
import com.app.HotelBooking.repository.HabitacionRepository;
import com.app.HotelBooking.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// ReservaServiceImpl.java
// Implementación del servicio de reservas
@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository, HabitacionRepository habitacionRepository) {
        this.reservaRepository = reservaRepository;
        this.habitacionRepository = habitacionRepository;
    }
// Implementación de los métodos definidos en ReservaService
    @Override
    public Reserva obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaException("Reserva no encontrada"));
    }
// Método para crear una nueva reserva
    // Verifica que la habitación esté libre antes de crear la reserva
    @Override
    public Reserva crearReserva(Reserva reserva, Long idHabitacion) {
        // Buscar la habitación por su id
        Habitacion habitacion = habitacionRepository.findById(idHabitacion)
                .orElseThrow(() -> new ReservaException("Habitación no encontrada"));

        // Verificar que la habitación esté libre
        if (!"libre".equalsIgnoreCase(habitacion.getEstado())) {
            throw new ReservaException("La habitación está ocupada");
        }

        // Asocia la reserva con la habitación y actualiza el estado
        habitacion.setReserva(reserva);
        habitacion.setEstado("ocupado");

        // Guarda la reserva y actualiza la habitación
        Reserva reservaGuardada = reservaRepository.save(reserva);
        habitacionRepository.save(habitacion);

        return reservaGuardada;
    }
// Método para actualizar una reserva existente
    // Permite actualizar los datos de una reserva existente
    @Override
    public Reserva actualizarReserva(Long id, Reserva reservaActualizada) {
        // Buscamos la reserva existente, de lo contrario, se lanza excepción
        Reserva reservaExistente = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaException("Reserva no encontrada"));

        // Actualizamos los campos requeridos
        reservaExistente.setNombre(reservaActualizada.getNombre());
        reservaExistente.setApellido(reservaActualizada.getApellido());
        reservaExistente.setDni(reservaActualizada.getDni());
        reservaExistente.setFechaDesde(reservaActualizada.getFechaDesde());
        reservaExistente.setFechaHasta(reservaActualizada.getFechaHasta());

        return reservaRepository.save(reservaExistente);
    }
    // Método para eliminar una reserva
    // Elimina una reserva y libera la habitación asociada
    @Override
    @Transactional
    public void eliminarReserva(Long id) {
        // Buscamos la reserva; si no existe, lanzamos una excepción
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaException("Reserva no encontrada"));
        
        // Actualizamos la habitación asociada, si existe, para dejarla libre
        Habitacion habitacion = reserva.getHabitacion();
        if (habitacion != null) {
            habitacion.setReserva(null);
            habitacion.setEstado("libre");
            habitacionRepository.save(habitacion);
        }
        
        // Eliminamos la reserva. Gracias a cascade, se eliminarán los consumos asociados.
        reservaRepository.delete(reserva);
    }
}
