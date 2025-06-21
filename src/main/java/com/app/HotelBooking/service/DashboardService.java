package com.app.HotelBooking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.HotelBooking.dto.DashboardDTO;
import com.app.HotelBooking.dto.DashboardDTO.ItemCount;
import com.app.HotelBooking.repository.DetalleFacturaRepository;
import com.app.HotelBooking.repository.FacturaRepository;

@Service
public class DashboardService {

    private final DetalleFacturaRepository detalleFacturaRepo;
    private final FacturaRepository        facturaRepo;

    @Autowired
    public DashboardService(
        DetalleFacturaRepository detalleFacturaRepo,
        FacturaRepository        facturaRepo
    ) {
        this.detalleFacturaRepo = detalleFacturaRepo;
        this.facturaRepo        = facturaRepo;
    }

    public DashboardDTO getStats() {
        DashboardDTO dto = new DashboardDTO();

        // 1) Productos más vendidos
        dto.topProductos = detalleFacturaRepo
          .sumCantidadByProducto()
          .stream()
          .map(o -> new ItemCount((String)o[0], (Long)o[1]))
          .toList();

        // 2) Habitaciones más facturadas (snapshot en factura.habitacionNombre)
        dto.topHabitaciones = facturaRepo
          .countByHabitacionNombre()
          .stream()
          .map(o -> new ItemCount((String)o[0], (Long)o[1]))
          .toList();

        // 3) Fechas de Reserva más frecuentes (snapshot en factura.fechaDesde)
        dto.topFechas = facturaRepo
          .countByFechaDesde()
          .stream()
          .map(o -> new ItemCount(o[0].toString(), (Long)o[1]))
          .toList();

        return dto;
    }
}
