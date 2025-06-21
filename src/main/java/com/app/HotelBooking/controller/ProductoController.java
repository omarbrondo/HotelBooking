package com.app.HotelBooking.controller;

import com.app.HotelBooking.model.Producto;
import com.app.HotelBooking.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // LISTAR todos
    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoRepository.findAll());
    }

    // OBTENER uno por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) {
        Optional<Producto> opt = productoRepository.findById(id);
        return opt
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // CREAR uno nuevo
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto nuevo) {
        Producto guardado = productoRepository.save(nuevo);
        return ResponseEntity.ok(guardado);
    }

    // ACTUALIZAR existente
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
        @PathVariable Long id,
        @RequestBody Producto datos
    ) {
        return productoRepository.findById(id)
            .map(existente -> {
                existente.setNombreProducto(datos.getNombreProducto());
                existente.setPrecio(datos.getPrecio());
                Producto actualizado = productoRepository.save(existente);
                return ResponseEntity.ok(actualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // BORRAR por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
