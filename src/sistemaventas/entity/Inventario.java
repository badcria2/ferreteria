/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.entity;

import java.time.LocalDateTime;

public class Inventario {
    private Integer idInventario;
    private Integer idProducto;
    private Integer idAlmacen;
    private Integer cantidad;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private LocalDateTime fechaActualizacion;
    private Boolean activo;
    
    // Objetos relacionados
    private Producto producto;
    private Almacen almacen;
    
    // Constructores
    public Inventario() {}
    
    public Inventario(Integer idProducto, Integer idAlmacen, Integer cantidad) {
        this.idProducto = idProducto;
        this.idAlmacen = idAlmacen;
        this.cantidad = cantidad;
        this.stockMinimo = 10;
        this.stockMaximo = 1000;
        this.activo = true;
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Integer getIdInventario() { return idInventario; }
    public void setIdInventario(Integer idInventario) { this.idInventario = idInventario; }
    
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    
    public Integer getIdAlmacen() { return idAlmacen; }
    public void setIdAlmacen(Integer idAlmacen) { this.idAlmacen = idAlmacen; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }
    
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    
    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }
    
    // Métodos de negocio
    public boolean isStockBajo() {
        return cantidad <= stockMinimo;
    }
    
    public boolean isStockAlto() {
        return cantidad >= stockMaximo;
    }
    
    public boolean puedeVender(int cantidadSolicitada) {
        return cantidad >= cantidadSolicitada;
    }
}