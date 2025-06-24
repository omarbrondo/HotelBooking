package com.app.HotelBooking.controller;

import com.app.HotelBooking.dto.DetalleConsumoDTO;
import com.app.HotelBooking.dto.ReservaDTO;
import com.app.HotelBooking.model.DetalleConsumo;
import com.app.HotelBooking.model.Producto;
import com.app.HotelBooking.model.Reserva;
import com.app.HotelBooking.repository.DetalleConsumoRepository;
import com.app.HotelBooking.repository.ProductoRepository;
import com.app.HotelBooking.service.ReservaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final ProductoRepository productoRepository;
    private final DetalleConsumoRepository detalleConsumoRepository;

    public ReservaController(
            ReservaService reservaService,
            ProductoRepository productoRepository,
            DetalleConsumoRepository detalleConsumoRepository) {
        this.reservaService = reservaService;
        this.productoRepository = productoRepository;
        this.detalleConsumoRepository = detalleConsumoRepository;
    }

    // Crear una nueva reserva
    @PostMapping
    public ResponseEntity<Reserva> crearReserva(@RequestBody ReservaDTO reservaDTO) {
        Reserva r = new Reserva();
        r.setNombre(reservaDTO.getNombre());
        r.setApellido(reservaDTO.getApellido());
        r.setDni(reservaDTO.getDni());
        r.setFechaDesde(reservaDTO.getFechaDesde());
        r.setFechaHasta(reservaDTO.getFechaHasta());

        Reserva guardada = reservaService.crearReserva(r, reservaDTO.getIdHabitacion());
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    // Obtener el detalle de una reserva con consumos
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerDetalleReserva(@PathVariable Long id) {
        Reserva reserva = reservaService.obtenerReservaPorId(id);
        return ResponseEntity.ok(reserva);
    }

    // Actualizar una reserva existente
    @PutMapping("/{id}")
    public ResponseEntity<Reserva> actualizarReserva(
            @PathVariable Long id,
            @RequestBody Reserva reservaActualizada) {
        Reserva actual = reservaService.actualizarReserva(id, reservaActualizada);
        return ResponseEntity.ok(actual);
    }

    // Registrar un consumo para la reserva
    @PostMapping("/{id}/consumos")
    public ResponseEntity<DetalleConsumo> agregarConsumo(
            @PathVariable Long id,
            @RequestBody DetalleConsumoDTO consumoDTO) {

        Reserva reserva = reservaService.obtenerReservaPorId(id);
        Producto producto = productoRepository.findById(consumoDTO.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        DetalleConsumo detalle = new DetalleConsumo(reserva, producto, consumoDTO.getCantidad());
        DetalleConsumo guardado = detalleConsumoRepository.save(detalle);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // Eliminar una reserva (y libera la habitación)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.noContent().build();
    }
}
