package com.app.HotelBooking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.HotelBooking.model.DetalleFactura;
import com.app.HotelBooking.model.Factura;
import com.app.HotelBooking.model.Reserva;
import com.app.HotelBooking.repository.FacturaRepository;
import com.app.HotelBooking.repository.ReservaRepository;

@Service
public class FacturaService {

    @Autowired
    private ReservaRepository reservaRepo;

    @Autowired
    private FacturaRepository facturaRepo;

    /**
     * Persiste una nueva factura solo si no existía ya una para esta reserva.
     * Almacena únicamente el ID de la reserva (reservaId) evitando el mapeo directo.
     * Se ejecuta en transacción independiente (REQUIRES_NEW).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Factura generarFactura(Long reservaId) {
        return facturaRepo
            // Busca por la columna reserva_id directamente
            .findByReservaId(reservaId)
            .orElseGet(() -> {
                // No existe: la creamos basándonos en la Reserva
                Reserva res = reservaRepo.findById(reservaId)
                    .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

                // 1) Cálculo de días
                long dias = ChronoUnit.DAYS.between(res.getFechaDesde(), res.getFechaHasta());

                // 2) Totales
                BigDecimal precioDia = BigDecimal.valueOf(res.getHabitacion().getPrecio());
                BigDecimal totalHab = precioDia.multiply(BigDecimal.valueOf(dias));

                BigDecimal totalCon = res.getConsumos().stream()
                    .map(c -> BigDecimal.valueOf(c.getProducto().getPrecio())
                                  .multiply(BigDecimal.valueOf(c.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalFinal = totalHab.add(totalCon);

                // 3) Armar la factura
                Factura fac = new Factura();
                fac.setReservaId(reservaId);
                fac.setFechaFactura(LocalDateTime.now());
                fac.setTotalHabitacion(totalHab);
                fac.setTotalConsumos(totalCon);
                fac.setTotalFinal(totalFinal);

                // 4) Armar los detalles
                for (var c : res.getConsumos()) {
                    BigDecimal precio = BigDecimal.valueOf(c.getProducto().getPrecio());
                    DetalleFactura df = new DetalleFactura();
                    df.setFactura(fac);
                    df.setProducto(c.getProducto());
                    df.setCantidad(c.getCantidad());
                    df.setPrecioUnitario(precio);
                    df.setSubtotal(precio.multiply(BigDecimal.valueOf(c.getCantidad())));
                    fac.getDetalles().add(df);
                }

                // 5) Guardar y devolver
                return facturaRepo.save(fac);
            });
    }
}
