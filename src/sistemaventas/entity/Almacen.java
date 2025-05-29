/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.entity;

import java.time.LocalDateTime;

public class Almacen {
    private Integer idAlmacen;
    private String descripcion;
    private String direccion;
    private String telefono;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
    
    // Constructores
    public Almacen() {}
    
    public Almacen(String descripcion, String direccion, String telefono) {
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.telefono = telefono;
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Integer getIdAlmacen() { return idAlmacen; }
    public void setIdAlmacen(Integer idAlmacen) { this.idAlmacen = idAlmacen; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    @Override
    public String toString() {
        return "Almacen{" +
                "idAlmacen=" + idAlmacen +
                ", descripcion='" + descripcion + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
