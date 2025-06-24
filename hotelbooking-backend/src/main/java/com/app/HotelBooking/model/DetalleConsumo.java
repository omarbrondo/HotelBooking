package com.app.HotelBooking.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class DetalleConsumo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación muchos a uno con Reserva
    @ManyToOne
    @JoinColumn(name = "reserva_id")
    @JsonBackReference  //para la serializacion y evitar bucles infinitos
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
        this.reserva = reserva; //para establecer la relación con la reserva
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
