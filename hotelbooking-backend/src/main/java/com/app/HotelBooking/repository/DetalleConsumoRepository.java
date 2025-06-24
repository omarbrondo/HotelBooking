// src/main/java/com/app/HotelBooking/repository/DetalleConsumoRepository.java
package com.app.HotelBooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.HotelBooking.model.DetalleConsumo;

public interface DetalleConsumoRepository extends JpaRepository<DetalleConsumo, Long> {

    /** Producto + suma de todas sus cantidades */
    @Query(""" 
      SELECT dc.producto.nombreProducto, SUM(dc.cantidad)
      FROM DetalleConsumo dc
      GROUP BY dc.producto.nombreProducto
      ORDER BY SUM(dc.cantidad) DESC 
      """) // Listar productos por cantidad consumida // más consumidos primero
    List<Object[]> sumCantidadByProducto();
}
