package com.app.HotelBooking.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import com.app.HotelBooking.dto.DashboardDTO;
import com.app.HotelBooking.dto.DashboardDTO.ItemCount;
import com.app.HotelBooking.repository.DetalleFacturaRepository;
import com.app.HotelBooking.repository.FacturaRepository;

@Service
public class DashboardService {

  private final DetalleFacturaRepository dfRepo;
  private final FacturaRepository        fRepo;

  public DashboardService(
    DetalleFacturaRepository dfRepo,
    FacturaRepository fRepo
  ) {
    this.dfRepo = dfRepo;
    this.fRepo  = fRepo;
  }

  public DashboardDTO getStats() {
    DashboardDTO dto = new DashboardDTO();

    // 1) Productos más consumidos
    dto.topProductos = dfRepo.sumCantidadByProducto().stream()
      .map(o -> new ItemCount((String)o[0], (Long)o[1]))
      .toList();

    // 2) Habitaciones más reservadas (desde factura.habitacionNombre)
    dto.topHabitaciones = fRepo.countByHabitacionNombre().stream()
      .map(o -> new ItemCount((String)o[0], (Long)o[1]))
      .toList();

    // 3) Fechas “Desde” más frecuentes (desde factura.fechaDesde)
    dto.topFechas = fRepo.countByFechaDesde().stream()
      .map(o -> new ItemCount(o[0].toString(), (Long)o[1]))
      .toList();

    // 4) Ingreso total
    BigDecimal total = fRepo.sumTotalFinal();
    dto.ingresoTotal = total == null ? BigDecimal.ZERO : total;

    return dto;
  }
}
