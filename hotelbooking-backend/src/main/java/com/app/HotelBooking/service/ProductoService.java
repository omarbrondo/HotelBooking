package com.app.HotelBooking.service;

import java.util.List;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.HotelBooking.model.Producto;
import com.app.HotelBooking.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository repo;

    /** 1) Lista solo los productos activos */
    public List<Producto> listarActivos() {
        return repo.findByActivoTrue();
    }

    /** 2) Lista todos los productos (activos e inactivos) */
    public List<Producto> listarTodos() {
        return repo.findAll();
    }

    /** 3) Crea un producto (activo = true por defecto) */
    public Producto crear(Producto nuevo) {
        nuevo.setActivo(true);
        return repo.save(nuevo);
    }

    /** 4) Actualiza nombre y precio de un producto existente */
    public Producto actualizar(Long id, Producto datos) {
        Producto existente = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Producto no existe: " + id));
        existente.setNombreProducto(datos.getNombreProducto());
        existente.setPrecio(datos.getPrecio());
        return repo.save(existente);
    }

    /** 5) Baja lógica: marca activo = false */
    public void eliminarLogico(Long id) {
        Producto p = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Producto no existe: " + id));
        p.setActivo(false);
        repo.save(p);
    }

    /** 6) Restaura un producto dado de baja (activo = true) */
    public void restaurar(Long id) {
        Producto p = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Producto no existe: " + id));
        p.setActivo(true);
        repo.save(p);
    }

    public Producto findById(Long id) {
  return repo.findById(id)
             .orElseThrow(() -> new EntityNotFoundException("Producto no existe: " + id));
}

}


