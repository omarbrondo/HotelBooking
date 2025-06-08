package com.app.HotelBooking.model;

import jakarta.persistence.*;

@Entity
public class DetalleConsumo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación muchos a uno con Reserva
    @ManyToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;
    
    // Relación muchos a uno con Producto
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;
    
    private Integer cantidad;
    
    // Constructores
    public DetalleConsumo() {}

    public DetalleConsumo(Reserva reserva, Producto producto, Integer cantidad) {
        this.reserva = reserva;
        this.producto = producto;
        this.cantidad = cantidad;
    }
    
    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Reserva getReserva() {
        return reserva;
    }
    
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
