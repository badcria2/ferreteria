/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Detalle de Venta
 * Incluye información del Inventario y Producto relacionados
 */
public class DetalleVentaDTO {
    private Integer idDetalleVenta;
    private Integer idVentas;
    private Integer idInventario;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal preciototal;
    private LocalDateTime fechaRegistro;
    
    // Información del Producto relacionado
    private String nombreProducto;
    private String descripcionProducto;
    private BigDecimal precioProductoActual;
    
    // Información del Almacén relacionado
    private String descripcionAlmacen;
    private Integer stockDisponible;
    
    // Campos calculados
    private String precioUnitarioFormateado;
    private String preciototalFormateado;
    private BigDecimal descuento;
    private Double porcentajeDescuento;
    
    // Constructores
    public DetalleVentaDTO() {}
    
    public DetalleVentaDTO(Integer idVentas, Integer idInventario, Integer cantidad, BigDecimal precioUnitario) {
        this.idVentas = idVentas;
        this.idInventario = idInventario;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.fechaRegistro = LocalDateTime.now();
        calcularTotal();
    }
    
    public DetalleVentaDTO(Integer idDetalleVenta, Integer idVentas, Integer idInventario, 
                          Integer cantidad, BigDecimal precioUnitario, BigDecimal preciototal) {
        this.idDetalleVenta = idDetalleVenta;
        this.idVentas = idVentas;
        this.idInventario = idInventario;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.preciototal = preciototal;
    }
    
    // Getters y Setters
    public Integer getIdDetalleVenta() {
        return idDetalleVenta;
    }
    
    public void setIdDetalleVenta(Integer idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }
    
    public Integer getIdVentas() {
        return idVentas;
    }
    
    public void setIdVentas(Integer idVentas) {
        this.idVentas = idVentas;
    }
    
    public Integer getIdInventario() {
        return idInventario;
    }
    
    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calcularTotal();
        calcularDescuento();
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularTotal();
        calcularDescuento();
    }
    
    public BigDecimal getPreciototal() {
        return preciototal;
    }
    
    public void setPreciototal(BigDecimal preciototal) {
        this.preciototal = preciototal;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    
    public String getDescripcionProducto() {
        return descripcionProducto;
    }
    
    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }
    
    public BigDecimal getPrecioProductoActual() {
        return precioProductoActual;
    }
    
    public void setPrecioProductoActual(BigDecimal precioProductoActual) {
        this.precioProductoActual = precioProductoActual;
        calcularDescuento();
    }
    
    public String getDescripcionAlmacen() {
        return descripcionAlmacen;
    }
    
    public void setDescripcionAlmacen(String descripcionAlmacen) {
        this.descripcionAlmacen = descripcionAlmacen;
    }
    
    public Integer getStockDisponible() {
        return stockDisponible;
    }
    
    public void setStockDisponible(Integer stockDisponible) {
        this.stockDisponible = stockDisponible;
    }
    
    public String getPrecioUnitarioFormateado() {
        return precioUnitario != null ? "S/ " + precioUnitario.toString() : "S/ 0.00";
    }
    
    public void setPrecioUnitarioFormateado(String precioUnitarioFormateado) {
        this.precioUnitarioFormateado = precioUnitarioFormateado;
    }
    
    public String getPreciototalFormateado() {
        return preciototal != null ? "S/ " + preciototal.toString() : "S/ 0.00";
    }
    
    public void setPreciototalFormateado(String preciototalFormateado) {
        this.preciototalFormateado = preciototalFormateado;
    }
    
    public BigDecimal getDescuento() {
        return descuento;
    }
    
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
    
    public Double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }
    
    public void setPorcentajeDescuento(Double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }
    
    // Métodos de utilidad y cálculo
    public void calcularTotal() {
        if (cantidad != null && precioUnitario != null) {
            this.preciototal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
    
    public boolean hayStockSuficiente() {
        return stockDisponible != null && cantidad != null && stockDisponible >= cantidad;
    }
    
    public boolean tieneDescuento() {
        return descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0;
    }
    
    private void calcularDescuento() {
        if (precioProductoActual != null && precioUnitario != null) {
            this.descuento = precioProductoActual.subtract(precioUnitario);
            if (precioProductoActual.compareTo(BigDecimal.ZERO) > 0) {
                this.porcentajeDescuento = descuento.divide(precioProductoActual, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
            }
        }
    }
    
    public String getDescuentoFormateado() {
        return descuento != null ? "S/ " + descuento.toString() : "S/ 0.00";
    }
    
    public String getPorcentajeDescuentoFormateado() {
        return porcentajeDescuento != null ? String.format("%.1f%%", porcentajeDescuento) : "0.0%";
    }
    
    public BigDecimal getSubtotal() {
        return preciototal;
    }
    
    @Override
    public String toString() {
        return "DetalleVentaDTO{" +
                "idDetalleVenta=" + idDetalleVenta +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", preciototal=" + preciototal +
                ", descripcionAlmacen='" + descripcionAlmacen + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        DetalleVentaDTO that = (DetalleVentaDTO) o;
        
        if (idDetalleVenta != null ? !idDetalleVenta.equals(that.idDetalleVenta) : that.idDetalleVenta != null) return false;
        if (idVentas != null ? !idVentas.equals(that.idVentas) : that.idVentas != null) return false;
        return idInventario != null ? idInventario.equals(that.idInventario) : that.idInventario == null;
    }
    
    @Override
    public int hashCode() {
        int result = idDetalleVenta != null ? idDetalleVenta.hashCode() : 0;
        result = 31 * result + (idVentas != null ? idVentas.hashCode() : 0);
        result = 31 * result + (idInventario != null ? idInventario.hashCode() : 0);
        return result;
    }
}
    