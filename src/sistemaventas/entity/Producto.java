/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Producto {
    private Integer idProducto;
    private String nombreproducto;
    private BigDecimal precio;
    private String descripcion;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
    
    // Constructores
    public Producto() {}
    
    public Producto(String nombreproducto, BigDecimal precio, String descripcion) {
        this.nombreproducto = nombreproducto;
        this.precio = precio;
        this.descripcion = descripcion;
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    
    public String getNombreproducto() { return nombreproducto; }
    public void setNombreproducto(String nombreproducto) { this.nombreproducto = nombreproducto; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombreproducto='" + nombreproducto + '\'' +
                ", precio=" + precio +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}