/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DetalleVenta {
    private Integer idDetalleVenta;
    private Integer idVentas;
    private Integer idInventario;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal preciototal;
    private LocalDateTime fechaRegistro;
    
    // Objetos relacionados
    private Venta venta;
    private Inventario inventario;
    
    // Constructores
    public DetalleVenta() {
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public DetalleVenta(Integer idVentas, Integer idInventario, Integer cantidad, BigDecimal precioUnitario) {
        this();
        this.idVentas = idVentas;
        this.idInventario = idInventario;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.calcularTotal();
    }
    
    // Getters y Setters
    public Integer getIdDetalleVenta() { return idDetalleVenta; }
    public void setIdDetalleVenta(Integer idDetalleVenta) { this.idDetalleVenta = idDetalleVenta; }
    
    public Integer getIdVentas() { return idVentas; }
    public void setIdVentas(Integer idVentas) { this.idVentas = idVentas; }
    
    public Integer getIdInventario() { return idInventario; }
    public void setIdInventario(Integer idInventario) { this.idInventario = idInventario; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { 
        this.cantidad = cantidad;
        calcularTotal();
    }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { 
        this.precioUnitario = precioUnitario;
        calcularTotal();
    }
    
    public BigDecimal getPreciototal() { return preciototal; }
    public void setPreciototal(BigDecimal preciototal) { this.preciototal = preciototal; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    
    public Inventario getInventario() { return inventario; }
    public void setInventario(Inventario inventario) { this.inventario = inventario; }
    
    // Métodos de negocio
    public void calcularTotal() {
        if (cantidad != null && precioUnitario != null) {
            this.preciototal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
}