// src/main/java/com/app/HotelBooking/dto/UsuarioDTO.java
package com.app.HotelBooking.dto;

public class UsuarioDTO {
    private Long idUsuario;
    private String nombreUsuario;
    private String rol;

    public UsuarioDTO() { }

    public UsuarioDTO(Long idUsuario, String nombreUsuario, String rol) {
        this.idUsuario     = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.rol           = rol;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
