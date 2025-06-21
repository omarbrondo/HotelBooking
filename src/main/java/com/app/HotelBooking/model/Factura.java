package com.app.HotelBooking.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDate;

@Entity
@Table(name = "factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Long id;

    @Column(name = "reserva_id", nullable = false)
    private Long reservaId;

    @Column(name = "fecha_factura", nullable = false)
    private LocalDateTime fechaFactura = LocalDateTime.now();

    @Column(name = "total_habitacion", nullable = false)
    private BigDecimal totalHabitacion;

    @Column(name = "total_consumos", nullable = false)
    private BigDecimal totalConsumos;

    @Column(name = "total_final", nullable = false)
    private BigDecimal totalFinal;

      @Column(name="habitacion_nombre", nullable=false)
  private String habitacionNombre;

  @Column(name = "fecha_desde", nullable = false)
  private LocalDate fechaDesde;

    @OneToMany(mappedBy = "factura", 
               cascade = CascadeType.ALL, 
               orphanRemoval = true)
    @JsonManagedReference               
    private List<DetalleFactura> detalles = new ArrayList<>();

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public LocalDateTime getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(LocalDateTime fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public BigDecimal getTotalHabitacion() {
        return totalHabitacion;
    }

    public void setTotalHabitacion(BigDecimal totalHabitacion) {
        this.totalHabitacion = totalHabitacion;
    }

    public BigDecimal getTotalConsumos() {
        return totalConsumos;
    }

    public void setTotalConsumos(BigDecimal totalConsumos) {
        this.totalConsumos = totalConsumos;
    }

    public BigDecimal getTotalFinal() {
        return totalFinal;
    }

    public void setTotalFinal(BigDecimal totalFinal) {
        this.totalFinal = totalFinal;
    }

    public List<DetalleFactura> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFactura> detalles) {
        this.detalles = detalles;
    }

      public String getHabitacionNombre() {
    return habitacionNombre;
  }

  public void setHabitacionNombre(String habitacionNombre) {
    this.habitacionNombre = habitacionNombre;
  }

  public LocalDate getFechaDesde() {
    return fechaDesde;
  }

  public void setFechaDesde(LocalDate fechaDesde) {
    this.fechaDesde = fechaDesde;
  }
}
