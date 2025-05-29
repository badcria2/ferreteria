/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Venta {
    private Integer idVentas;
    private LocalDate fecha;
    private Integer idCliente;
    private BigDecimal total;
    private EstadoVenta estado;
    private LocalDateTime fechaRegistro;
    
    // Objetos relacionados
    private Cliente cliente;
    private List<DetalleVenta> detalles;
    
    // Enum para estado de venta
    public enum EstadoVenta {
        PENDIENTE, COMPLETADA, CANCELADA
    }
    
    // Constructores
    public Venta() {
        this.estado = EstadoVenta.PENDIENTE;
        this.total = BigDecimal.ZERO;
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public Venta(Integer idCliente, LocalDate fecha) {
        this();
        this.idCliente = idCliente;
        this.fecha = fecha;
    }
    
    // Getters y Setters
    public Integer getIdVentas() { return idVentas; }
    public void setIdVentas(Integer idVentas) { this.idVentas = idVentas; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public EstadoVenta getEstado() { return estado; }
    public void setEstado(EstadoVenta estado) { this.estado = estado; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
    
    // Métodos de negocio
    public void calcularTotal() {
        if (detalles != null) {
            total = detalles.stream()
                    .map(DetalleVenta::getPreciototal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
    
    public boolean puedeSerCancelada() {
        return estado == EstadoVenta.PENDIENTE;
    }
    
    public void completar() {
        this.estado = EstadoVenta.COMPLETADA;
    }
    
    public void cancelar() {
        if (puedeSerCancelada()) {
            this.estado = EstadoVenta.CANCELADA;
        }
    }
}
