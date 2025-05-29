/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.entity;


import java.time.LocalDateTime;

public class Cliente {
    private Integer idCliente;
    private String nombre;
    private String nroDocumento;
    private String correo;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
    
    // Constructores
    public Cliente() {}
    
    public Cliente(String nombre, String nroDocumento, String correo) {
        this.nombre = nombre;
        this.nroDocumento = nroDocumento;
        this.correo = correo;
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getNroDocumento() { return nroDocumento; }
    public void setNroDocumento(String nroDocumento) { this.nroDocumento = nroDocumento; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", nombre='" + nombre + '\'' +
                ", nroDocumento='" + nroDocumento + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}