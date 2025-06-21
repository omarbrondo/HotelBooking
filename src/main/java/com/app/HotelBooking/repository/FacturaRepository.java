package com.app.HotelBooking.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.HotelBooking.model.Factura;

@Repository
public interface FacturaRepository 
     extends JpaRepository<Factura, Long> {

  // Busca factura por la columna reserva_id
  Optional<Factura> findByReservaId(Long reservaId);
      @Query("SELECT SUM(f.totalFinal) FROM Factura f")
    BigDecimal sumTotalFinal();

}
