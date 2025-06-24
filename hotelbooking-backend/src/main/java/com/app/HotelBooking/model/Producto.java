package com.app.HotelBooking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long idProducto;
    
    private String nombreProducto;
    
    private Double precio;

      @Column(nullable=false)
  private boolean activo = true;
    
    // Constructores
    public Producto() {}
    
    public Producto(String nombreProducto, Double precio) {
        this.nombreProducto = nombreProducto;
        this.precio = precio;
    }
    
    // Getters y setters
    public Long getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }
    
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    
    public Double getPrecio() {
        return precio;
    }
    
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

     public boolean isActivo() { return activo; }
  public void setActivo(boolean activo) { this.activo = activo; }
}
