package com.app.HotelBooking.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.HotelBooking.model.DetalleConsumo;
import com.app.HotelBooking.model.DetalleFactura;

public interface DetalleConsumoRepository 
        extends JpaRepository<DetalleConsumo, Long> {

    /**
     * Devuelve nombre de producto y total de unidades consumidas,
     * ordenado de mayor a menor.
     */
    @Query("""
      SELECT dc.producto.nombreProducto, SUM(dc.cantidad)
      FROM DetalleConsumo dc
      GROUP BY dc.producto.nombreProducto
      ORDER BY SUM(dc.cantidad) DESC
      """)
    List<Object[]> sumCantidadByProducto();

    /**
     * Suma todos los subtotales de la tabla DetalleFactura 
     * (para incluir ingresos por consumos en el dashboard).
     */
    @Query("SELECT SUM(d.subtotal) FROM DetalleFactura d")
    BigDecimal sumTotalSubtotales();
}
