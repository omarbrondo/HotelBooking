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
import com.app.HotelBooking.model.Habitacion;
import com.app.HotelBooking.repository.FacturaRepository;
import com.app.HotelBooking.repository.HabitacionRepository;
import com.app.HotelBooking.repository.ReservaRepository;



@Service
public class CheckoutService {

    @Autowired private ReservaRepository reservaRepo;
    @Autowired private HabitacionRepository habitacionRepo;
    @Autowired private FacturaRepository facturaRepo;

    /**
     * 1) Genera la factura en una TX nueva (aislada del borrado).
     * 2) Libera la habitación (set reserva_id = null).
     * 3) Elimina la reserva.
     */
    @Transactional
    public Factura checkout(Long reservaId) {
        // 1) Generamos la factura (aislado de esta TX)
        Factura fac = generarFactura(reservaId);

        // 2) Liberamos la habitación
        Reserva res = reservaRepo.findById(reservaId)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        Habitacion hab = res.getHabitacion();
        hab.setReserva(null);
        hab.setEstado("libre");
        habitacionRepo.save(hab);

        // 3) Borramos la reserva
        reservaRepo.deleteById(reservaId);

        return fac;
    }
// Genera la factura de forma aislada (idempotente)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected Factura generarFactura(Long reservaId) {
        // Si ya existe, devolverla (idempotente)
        return facturaRepo.findByReservaId(reservaId)
          .orElseGet(() -> {
            Reserva res = reservaRepo.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

            // Cálculo días y totales
            long dias = ChronoUnit.DAYS.between(res.getFechaDesde(), res.getFechaHasta());
            BigDecimal precioDia = BigDecimal.valueOf(res.getHabitacion().getPrecio());
            BigDecimal totalHab = precioDia.multiply(BigDecimal.valueOf(dias));
            BigDecimal totalCon = res.getConsumos().stream()
                .map(c -> BigDecimal.valueOf(c.getProducto().getPrecio())
                                 .multiply(BigDecimal.valueOf(c.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalFinal = totalHab.add(totalCon);

            // Armar Factura
            Factura fac = new Factura();
            fac.setReservaId(reservaId);
            fac.setFechaFactura(LocalDateTime.now());
            fac.setTotalHabitacion(totalHab);
            fac.setTotalConsumos(totalCon);
            fac.setTotalFinal(totalFinal);
            fac.setHabitacionNombre(res.getHabitacion().getNombreHabitacion());
            fac.setHabitacionNombre(res.getHabitacion().getNombreHabitacion());
            fac.setFechaDesde(res.getFechaDesde());


            // Detalles
            res.getConsumos().forEach(c -> {
                BigDecimal p = BigDecimal.valueOf(c.getProducto().getPrecio());
                DetalleFactura df = new DetalleFactura();
                df.setFactura(fac);
                df.setProducto(c.getProducto());
                df.setCantidad(c.getCantidad());
                df.setPrecioUnitario(p);
                df.setSubtotal(p.multiply(BigDecimal.valueOf(c.getCantidad())));
                fac.getDetalles().add(df);
            });

            return facturaRepo.save(fac);
        });
    }
}
