package com.app.HotelBooking.model;

import jakarta.persistence.*;

@Entity
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHabitacion;

    private String nombreHabitacion;
    private Double precio;
    private String estado; // "libre" o "ocupado"

    @OneToOne
    @JoinColumn(name = "idReserva", unique = true)
    private Reserva reserva;

    public Habitacion() {}

    public Habitacion(String nombreHabitacion, Double precio, String estado) {
        this.nombreHabitacion = nombreHabitacion;
        this.precio = precio;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(Long idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public String getNombreHabitacion() {
        return nombreHabitacion;
    }

    public void setNombreHabitacion(String nombreHabitacion) {
        this.nombreHabitacion = nombreHabitacion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
}
