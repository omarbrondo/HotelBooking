package com.app.HotelBooking.dto;

import java.time.LocalDate;

public class ReservaDTO {
    private String nombre;
    private String apellido;
    private String dni;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Long idHabitacion; // Identificador de la habitación a reservar

    public ReservaDTO() {
    }

    // Getters y Setters
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
    public Long getIdHabitacion() {
        return idHabitacion;
    }
    public void setIdHabitacion(Long idHabitacion) {
        this.idHabitacion = idHabitacion;
    }
}
