package com.app.HotelBooking.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import jakarta.persistence.*;
import com.app.HotelBooking.model.Producto;
import com.app.HotelBooking.repository.ProductoRepository;


@Service
public class ProductoService {
  @Autowired private ProductoRepository repo;

  // 1) Listar solo activos
  public List<Producto> listarActivos() {
    return repo.findByActivoTrue();
  }

  // 2) Crear uno nuevo (activo por defecto)
  public Producto crear(Producto nuevo) {
    // Asegúrate de que activo esté en true por defecto
    nuevo.setActivo(true);
    return repo.save(nuevo);
  }

  // 3) Actualizar existente (solo si existe)
  public Producto actualizar(Long id, Producto datos) {
    Producto existente = repo.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Producto no existe"));
    existente.setNombreProducto(datos.getNombreProducto());
    existente.setPrecio(datos.getPrecio());
    // No tocamos el flag 'activo'
    return repo.save(existente);
  }

  // 4) Borrado lógico
  public void eliminarLogico(Long id) {
    Producto p = repo.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Producto no existe"));
    p.setActivo(false);
    repo.save(p);
  }

  // 5) Restaurar (opcional)
  public void restaurar(Long id) {
    Producto p = repo.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Producto no existe"));
    p.setActivo(true);
    repo.save(p);
  }
}
