package com.app.HotelBooking.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;            // ← Import necesario

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.HotelBooking.model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    /** 1) Búsqueda de factura por reservaId (necesario para checkout idempotente) */
    Optional<Factura> findByReservaId(Long reservaId);

    /** 2) Suma total de las habitaciones facturadas */
    @Query("SELECT SUM(f.totalHabitacion) FROM Factura f")
    BigDecimal sumTotalHabitacion();

    /** 3) Conteo de facturas por habitación (para dashboard) */
    @Query("""
      SELECT f.habitacionNombre, COUNT(f)
      FROM Factura f
      GROUP BY f.habitacionNombre
      ORDER BY COUNT(f) DESC
      """)
    List<Object[]> countByHabitacionNombre();

    /** 4) Conteo de facturas por fechaDesde (para dashboard fechas) */
    @Query("""
      SELECT f.fechaDesde, COUNT(f)
      FROM Factura f
      GROUP BY f.fechaDesde
      ORDER BY COUNT(f) DESC
      """)
    List<Object[]> countByFechaDesde();
}
