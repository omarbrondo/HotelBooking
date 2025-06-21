package com.app.HotelBooking.controller;

import com.app.HotelBooking.model.Producto;
import com.app.HotelBooking.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

  private final ProductoService productoService;

  @Autowired
  public ProductoController(ProductoService productoService) {
    this.productoService = productoService;
  }

  // LISTAR solo los activos
  @GetMapping
  public ResponseEntity<List<Producto>> listar() {
    List<Producto> activos = productoService.listarActivos();
    return ResponseEntity.ok(activos);
  }

  // OBTENER uno por ID (solo si está activo)
  @GetMapping("/{id}")
  public ResponseEntity<Producto> obtener(@PathVariable Long id) {
    Optional<Producto> opt = productoService
      .listarActivos()
      .stream()
      .filter(p -> p.getIdProducto().equals(id))
      .findFirst();

    return opt
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  // CREAR uno nuevo (activo por defecto)
  @PostMapping
  public ResponseEntity<Producto> crear(@RequestBody Producto nuevo) {
    Producto guardado = productoService.crear(nuevo);
    return ResponseEntity.ok(guardado);
  }

  // ACTUALIZAR existente (solo activo)
  @PutMapping("/{id}")
  public ResponseEntity<Producto> actualizar(
    @PathVariable Long id,
    @RequestBody Producto datos
  ) {
    try {
      Producto actualizado = productoService.actualizar(id, datos);
      return ResponseEntity.ok(actualizado);
    } catch (EntityNotFoundException ex) {
      return ResponseEntity.notFound().build();
    }
  }

  // BORRAR lógicamente (activo = false)
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> borrar(@PathVariable Long id) {
    try {
      productoService.eliminarLogico(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException ex) {
      return ResponseEntity.notFound().build();
    }
  }

  // (Opcional) ENDPOINT para restaurar un producto
  @PatchMapping("/{id}/restaurar")
  public ResponseEntity<Void> restaurar(@PathVariable Long id) {
    try {
      productoService.restaurar(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException ex) {
      return ResponseEntity.notFound().build();
    }
  }
}
