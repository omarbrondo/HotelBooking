package com.app.HotelBooking.controller;

import com.app.HotelBooking.model.Producto;
import com.app.HotelBooking.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductoController {

    private final ProductoRepository productoRepository;

    @Autowired
    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> listarProductos() {
        // Obtiene todos los productos de la base de datos
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }
}
