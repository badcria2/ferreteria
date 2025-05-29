/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dto;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Cliente
 * Utilizado para comunicación entre capas
 */
public class ClienteDTO {
    private Integer idCliente;
    private String nombre;
    private String nroDocumento;
    private String correo;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
    
    // Constructores
    public ClienteDTO() {}
    
    public ClienteDTO(String nombre, String nroDocumento, String correo) {
        this.nombre = nombre;
        this.nroDocumento = nroDocumento;
        this.correo = correo;
        this.activo = true;
    }
    
    public ClienteDTO(Integer idCliente, String nombre, String nroDocumento, String correo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.nroDocumento = nroDocumento;
        this.correo = correo;
        this.activo = true;
    }
    
    // Getters y Setters
    public Integer getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getNroDocumento() {
        return nroDocumento;
    }
    
    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    // Métodos de utilidad
    public boolean isActivo() {
        return activo != null && activo;
    }
    
    @Override
    public String toString() {
        return "ClienteDTO{" +
                "idCliente=" + idCliente +
                ", nombre='" + nombre + '\'' +
                ", nroDocumento='" + nroDocumento + '\'' +
                ", correo='" + correo + '\'' +
                ", activo=" + activo +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ClienteDTO that = (ClienteDTO) o;
        
        if (idCliente != null ? !idCliente.equals(that.idCliente) : that.idCliente != null) return false;
        return nroDocumento != null ? nroDocumento.equals(that.nroDocumento) : that.nroDocumento == null;
    }
    
    @Override
    public int hashCode() {
        int result = idCliente != null ? idCliente.hashCode() : 0;
        result = 31 * result + (nroDocumento != null ? nroDocumento.hashCode() : 0);
        return result;
    }
}
