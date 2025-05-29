/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Producto
 */
public class ProductoDTO {
    private Integer idProducto;
    private String nombreproducto;
    private BigDecimal precio;
    private String descripcion;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
    
    // Campos adicionales para presentación
    private String precioFormateado;
    private Integer stockTotal; // Suma de todos los inventarios
    
    // Constructores
    public ProductoDTO() {}
    
    public ProductoDTO(String nombreproducto, BigDecimal precio, String descripcion) {
        this.nombreproducto = nombreproducto;
        this.precio = precio;
        this.descripcion = descripcion;
        this.activo = true;
    }
    
    public ProductoDTO(Integer idProducto, String nombreproducto, BigDecimal precio, String descripcion) {
        this.idProducto = idProducto;
        this.nombreproducto = nombreproducto;
        this.precio = precio;
        this.descripcion = descripcion;
        this.activo = true;
    }
    
    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
    
    public String getNombreproducto() {
        return nombreproducto;
    }
    
    public void setNombreproducto(String nombreproducto) {
        this.nombreproducto = nombreproducto;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
    
    public String getPrecioFormateado() {
        return precioFormateado;
    }
    
    public void setPrecioFormateado(String precioFormateado) {
        this.precioFormateado = precioFormateado;
    }
    
    public Integer getStockTotal() {
        return stockTotal;
    }
    
    public void setStockTotal(Integer stockTotal) {
        this.stockTotal = stockTotal;
    }
    
    // Métodos de utilidad
    public boolean isActivo() {
        return activo != null && activo;
    }
    
    public String getPrecioConMoneda() {
        return precio != null ? "S/ " + precio.toString() : "S/ 0.00";
    }
    
    @Override
    public String toString() {
        return "ProductoDTO{" +
                "idProducto=" + idProducto +
                ", nombreproducto='" + nombreproducto + '\'' +
                ", precio=" + precio +
                ", descripcion='" + descripcion + '\'' +
                ", activo=" + activo +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ProductoDTO that = (ProductoDTO) o;
        
        if (idProducto != null ? !idProducto.equals(that.idProducto) : that.idProducto != null) return false;
        return nombreproducto != null ? nombreproducto.equals(that.nombreproducto) : that.nombreproducto == null;
    }
    
    @Override
    public int hashCode() {
        int result = idProducto != null ? idProducto.hashCode() : 0;
        result = 31 * result + (nombreproducto != null ? nombreproducto.hashCode() : 0);
        return result;
    }
}
