package com.app.HotelBooking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.app.HotelBooking.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
// Métodos de consulta personalizados para la entidad Reserva
  @Query("""
    SELECT r.habitacion.nombreHabitacion, COUNT(r)
    FROM Reserva r
    GROUP BY r.habitacion.nombreHabitacion
    ORDER BY COUNT(r) DESC
  """)
  List<Object[]> countByHabitacion();
// Método para contar reservas por fecha de inicio
  @Query("""
    SELECT r.fechaDesde, COUNT(r)
    FROM Reserva r
    GROUP BY r.fechaDesde
    ORDER BY COUNT(r) DESC
  """)
  List<Object[]> countByFechaDesde();
}
