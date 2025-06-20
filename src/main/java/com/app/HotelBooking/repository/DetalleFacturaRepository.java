package com.app.HotelBooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.HotelBooking.model.DetalleFactura;  // <-- Import necesario

@Repository  // opcional, Spring lo detecta igual al extender JpaRepository
public interface DetalleFacturaRepository 
    extends JpaRepository<DetalleFactura, Long> {
}
