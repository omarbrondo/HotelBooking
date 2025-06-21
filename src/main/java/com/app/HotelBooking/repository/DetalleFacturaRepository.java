package com.app.HotelBooking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.app.HotelBooking.model.DetalleFactura;

public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {

  @Query("""
    SELECT df.producto.nombreProducto, SUM(df.cantidad)
    FROM DetalleFactura df
    GROUP BY df.producto.nombreProducto
    ORDER BY SUM(df.cantidad) DESC
  """)
  List<Object[]> sumCantidadByProducto();
}
