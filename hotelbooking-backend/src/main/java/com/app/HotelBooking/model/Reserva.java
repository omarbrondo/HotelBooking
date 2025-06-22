package com.app.HotelBooking.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    private String nombre;
    private String apellido;
    private String dni;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    @OneToOne(mappedBy = "reserva")
    @JsonIgnore
    private Habitacion habitacion;

    @OneToMany(mappedBy = "reserva", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference  // Se incluyen los detalles de consumo al serializar la reserva
    private List<DetalleConsumo> consumos;

    public Reserva() {}

    public Reserva(String nombre, String apellido, String dni, LocalDate fechaDesde, LocalDate fechaHasta) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public List<DetalleConsumo> getConsumos() {
        return consumos;
    }

    public void setConsumos(List<DetalleConsumo> consumos) {
        this.consumos = consumos;
    }
}
