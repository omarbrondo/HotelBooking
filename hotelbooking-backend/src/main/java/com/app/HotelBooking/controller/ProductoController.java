package com.app.HotelBooking.controller;

import java.util.List;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.HotelBooking.model.Producto;
import com.app.HotelBooking.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // 1.1) Listar activos
    @GetMapping
    public ResponseEntity<List<Producto>> listarActivos() {
        return ResponseEntity.ok(service.listarActivos());
    }

    // 1.2) Listar todos (activos e inactivos)
    @GetMapping("/all")
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // Obtener uno por ID 
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        try {
            Producto p = service.findById(id);
            return ResponseEntity.ok(p);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // 1.4) Crear
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto nuevo) {
        return ResponseEntity.ok(service.crear(nuevo));
    }

    // 1.5) Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
        @PathVariable Long id,
        @RequestBody Producto datos
    ) {
        try {
            Producto updated = service.actualizar(id, datos);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // 1.6) Baja lógica
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> bajaLogica(@PathVariable Long id) {
        try {
            service.eliminarLogico(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // 1.7) Restaurar
    @PatchMapping("/{id}/restaurar")
    public ResponseEntity<Void> restaurar(@PathVariable Long id) {
        try {
            service.restaurar(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
