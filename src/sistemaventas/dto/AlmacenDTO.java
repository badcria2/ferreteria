/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dto;

import java.time.LocalDateTime;

public class AlmacenDTO {
    private Integer idAlmacen;
    private String descripcion;
    private String direccion;
    private String telefono;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
    
    // Campos adicionales para presentación
    private Integer totalProductos;
    private Integer totalStock;
    
    // Constructores
    public AlmacenDTO() {}
    
    public AlmacenDTO(String descripcion, String direccion, String telefono) {
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.telefono = telefono;
        this.activo = true;
    }
    
    public AlmacenDTO(Integer idAlmacen, String descripcion, String direccion, String telefono) {
        this.idAlmacen = idAlmacen;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.telefono = telefono;
        this.activo = true;
    }
    
    // Getters y Setters
    public Integer getIdAlmacen() {
        return idAlmacen;
    }
    
    public void setIdAlmacen(Integer idAlmacen) {
        this.idAlmacen = idAlmacen;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
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
    
    public Integer getTotalProductos() {
        return totalProductos;
    }
    
    public void setTotalProductos(Integer totalProductos) {
        this.totalProductos = totalProductos;
    }
    
    public Integer getTotalStock() {
        return totalStock;
    }
    
    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }
    
    // Métodos de utilidad
    public boolean isActivo() {
        return activo != null && activo;
    }
    
    @Override
    public String toString() {
        return "AlmacenDTO{" +
                "idAlmacen=" + idAlmacen +
                ", descripcion='" + descripcion + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", activo=" + activo +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AlmacenDTO that = (AlmacenDTO) o;
        
        if (idAlmacen != null ? !idAlmacen.equals(that.idAlmacen) : that.idAlmacen != null) return false;
        return descripcion != null ? descripcion.equals(that.descripcion) : that.descripcion == null;
    }
    
    @Override
    public int hashCode() {
        int result = idAlmacen != null ? idAlmacen.hashCode() : 0;
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        return result;
    }
}
