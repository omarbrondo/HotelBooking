package com.app.HotelBooking.repository;

import com.app.HotelBooking.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
