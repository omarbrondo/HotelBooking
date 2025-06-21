package com.app.HotelBooking.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.HotelBooking.dto.DashboardDTO;
import com.app.HotelBooking.dto.DashboardDTO.ItemCount;
import com.app.HotelBooking.repository.DetalleConsumoRepository;
import com.app.HotelBooking.repository.FacturaRepository;
import com.app.HotelBooking.repository.ReservaRepository;

@Service
public class DashboardService {

    private final DetalleConsumoRepository consumoRepo;
    private final ReservaRepository        reservaRepo;
    private final FacturaRepository        facturaRepo;

    @Autowired
    public DashboardService(
        DetalleConsumoRepository consumoRepo,
        ReservaRepository        reservaRepo,
        FacturaRepository        facturaRepo
    ) {
        this.consumoRepo = consumoRepo;
        this.reservaRepo = reservaRepo;
        this.facturaRepo = facturaRepo;
    }

    public DashboardDTO getStats() {
        DashboardDTO dto = new DashboardDTO();

        dto.topProductos = consumoRepo
          .sumCantidadByProducto()
          .stream()
          .map(o -> new ItemCount((String) o[0], (Long) o[1]))
          .toList();

        dto.topHabitaciones = reservaRepo
          .countByHabitacion()
          .stream()
          .map(o -> new ItemCount((String) o[0], (Long) o[1]))
          .toList();

        dto.topFechas = reservaRepo
          .countByFechaDesde()
          .stream()
          .map(o -> new ItemCount(o[0].toString(), (Long) o[1]))
          .toList();

        BigDecimal ingresoHab = facturaRepo.sumTotalFinal();
        BigDecimal ingresoCon = consumoRepo.sumTotalSubtotales();
        dto.ingresoTotal = ingresoHab
            .add(ingresoCon == null ? BigDecimal.ZERO : ingresoCon);

        return dto;
    }
}
