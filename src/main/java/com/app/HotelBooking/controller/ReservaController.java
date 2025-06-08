package com.app.HotelBooking.controller;

import com.app.HotelBooking.dto.DetalleConsumoDTO;
import com.app.HotelBooking.dto.ReservaDTO;
import com.app.HotelBooking.exception.ReservaException;
import com.app.HotelBooking.model.DetalleConsumo;
import com.app.HotelBooking.model.Habitacion;
import com.app.HotelBooking.model.Producto;
import com.app.HotelBooking.model.Reserva;
import com.app.HotelBooking.repository.DetalleConsumoRepository;
import com.app.HotelBooking.repository.ProductoRepository;
import com.app.HotelBooking.service.HabitacionService;
import com.app.HotelBooking.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReservaController {

    private final HabitacionService habitacionService;
    private final ReservaService reservaService;
    private final ProductoRepository productoRepository;
    private final DetalleConsumoRepository detalleConsumoRepository;

    @Autowired
    public ReservaController(HabitacionService habitacionService,
            ReservaService reservaService,
            ProductoRepository productoRepository,
            DetalleConsumoRepository detalleConsumoRepository) {
        this.habitacionService = habitacionService;
        this.reservaService = reservaService;
        this.productoRepository = productoRepository;
        this.detalleConsumoRepository = detalleConsumoRepository;
    }

    // Endpoint para obtener las habitaciones libres
    @GetMapping("/habitaciones/libres")
    public ResponseEntity<List<Habitacion>> getHabitacionesLibres() {
        List<Habitacion> habitacionesLibres = habitacionService.obtenerHabitacionesLibres();
        return ResponseEntity.ok(habitacionesLibres);
    }

    // Endpoint para actualizar una reserva existente
    @PutMapping("/reservas/{id}")
    public ResponseEntity<Reserva> actualizarReserva(@PathVariable Long id, @RequestBody Reserva reservaActualizada) {
        Reserva reservaActual = reservaService.actualizarReserva(id, reservaActualizada);
        return ResponseEntity.ok(reservaActual);
    }

    // Endpoint para registrar una nueva reserva
    @PostMapping("/reservas")
    public ResponseEntity<Reserva> crearReserva(@RequestBody ReservaDTO reservaDTO) {
        // Convertir el DTO a la entidad Reserva
        Reserva reserva = new Reserva();
        reserva.setNombre(reservaDTO.getNombre());
        reserva.setApellido(reservaDTO.getApellido());
        reserva.setDni(reservaDTO.getDni());
        reserva.setFechaDesde(reservaDTO.getFechaDesde());
        reserva.setFechaHasta(reservaDTO.getFechaHasta());

        Reserva reservaGuardada = reservaService.crearReserva(reserva, reservaDTO.getIdHabitacion());
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaGuardada);
    }

    // Endpoint para registrar un consumo (detalle consumo) para una reserva
    @PostMapping("/reservas/{id}/consumos")
    public ResponseEntity<DetalleConsumo> agregarConsumo(@PathVariable Long id,
            @RequestBody DetalleConsumoDTO consumoDTO) {
        // Buscamos la reserva por id (asegúrate de tener este método implementado en
        // ReservaService)
        Reserva reserva = reservaService.obtenerReservaPorId(id);

        // Buscamos el producto por su id
        Producto producto = productoRepository.findById(consumoDTO.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Creamos el detalle de consumo con la cantidad indicada
        DetalleConsumo detalleConsumo = new DetalleConsumo(reserva, producto, consumoDTO.getCantidad());
        DetalleConsumo detalleGuardado = detalleConsumoRepository.save(detalleConsumo);

        return ResponseEntity.status(HttpStatus.CREATED).body(detalleGuardado);
    }
}
