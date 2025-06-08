package com.app.HotelBooking.dto;

public class DetalleConsumoDTO {
    private Long idProducto;
    private Integer cantidad;

    // Getters y Setters
    public Long getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
