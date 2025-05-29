/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Inventario
 * Incluye información de Producto y Almacén relacionados
 */
public class InventarioDTO {
    private Integer idInventario;
    private Integer idProducto;
    private Integer idAlmacen;
    private Integer cantidad;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private LocalDateTime fechaActualizacion;
    private Boolean activo;
    
    // Información del Producto relacionado
    private String nombreProducto;
    private BigDecimal precioProducto;
    private String descripcionProducto;
    
    // Información del Almacén relacionado
    private String descripcionAlmacen;
    private String direccionAlmacen;
    private String telefonoAlmacen;
    
    // Campos calculados
    private String estadoStock;
    private BigDecimal valorInventario;
    private Double porcentajeStock;
    
    // Constructores
    public InventarioDTO() {}
    
    public InventarioDTO(Integer idProducto, Integer idAlmacen, Integer cantidad) {
        this.idProducto = idProducto;
        this.idAlmacen = idAlmacen;
        this.cantidad = cantidad;
        this.stockMinimo = 10;
        this.stockMaximo = 1000;
        this.activo = true;
    }
    
    public InventarioDTO(Integer idProducto, Integer idAlmacen, Integer cantidad, 
                        Integer stockMinimo, Integer stockMaximo) {
        this.idProducto = idProducto;
        this.idAlmacen = idAlmacen;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.activo = true;
    }
    
    // Getters y Setters
    public Integer getIdInventario() {
        return idInventario;
    }
    
    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }
    
    public Integer getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
    
    public Integer getIdAlmacen() {
        return idAlmacen;
    }
    
    public void setIdAlmacen(Integer idAlmacen) {
        this.idAlmacen = idAlmacen;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calcularValorInventario();
        calcularPorcentajeStock();
    }
    
    public Integer getStockMinimo() {
        return stockMinimo;
    }
    
    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
        calcularPorcentajeStock();
    }
    
    public Integer getStockMaximo() {
        return stockMaximo;
    }
    
    public void setStockMaximo(Integer stockMaximo) {
        this.stockMaximo = stockMaximo;
        calcularPorcentajeStock();
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    
    public BigDecimal getPrecioProducto() {
        return precioProducto;
    }
    
    public void setPrecioProducto(BigDecimal precioProducto) {
        this.precioProducto = precioProducto;
        calcularValorInventario();
    }
    
    public String getDescripcionProducto() {
        return descripcionProducto;
    }
    
    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }
    
    public String getDescripcionAlmacen() {
        return descripcionAlmacen;
    }
    
    public void setDescripcionAlmacen(String descripcionAlmacen) {
        this.descripcionAlmacen = descripcionAlmacen;
    }
    
    public String getDireccionAlmacen() {
        return direccionAlmacen;
    }
    
    public void setDireccionAlmacen(String direccionAlmacen) {
        this.direccionAlmacen = direccionAlmacen;
    }
    
    public String getTelefonoAlmacen() {
        return telefonoAlmacen;
    }
    
    public void setTelefonoAlmacen(String telefonoAlmacen) {
        this.telefonoAlmacen = telefonoAlmacen;
    }
    
    public String getEstadoStock() {
        if (estadoStock == null) {
            calcularEstadoStock();
        }
        return estadoStock;
    }
    
    public void setEstadoStock(String estadoStock) {
        this.estadoStock = estadoStock;
    }
    
    public BigDecimal getValorInventario() {
        if (valorInventario == null) {
            calcularValorInventario();
        }
        return valorInventario;
    }
    
    public void setValorInventario(BigDecimal valorInventario) {
        this.valorInventario = valorInventario;
    }
    
    public Double getPorcentajeStock() {
        if (porcentajeStock == null) {
            calcularPorcentajeStock();
        }
        return porcentajeStock;
    }
    
    public void setPorcentajeStock(Double porcentajeStock) {
        this.porcentajeStock = porcentajeStock;
    }
    
    // Métodos de utilidad y cálculo
    public boolean isActivo() {
        return activo != null && activo;
    }
    
    public boolean isStockBajo() {
        return cantidad != null && stockMinimo != null && cantidad <= stockMinimo;
    }
    
    public boolean isStockAlto() {
        return cantidad != null && stockMaximo != null && cantidad >= stockMaximo;
    }
    
    public boolean isStockNormal() {
        return !isStockBajo() && !isStockAlto();
    }
    
    public boolean puedeVender(Integer cantidadSolicitada) {
        return cantidad != null && cantidadSolicitada != null && cantidad >= cantidadSolicitada;
    }
    
    private void calcularEstadoStock() {
        if (isStockBajo()) {
            this.estadoStock = "BAJO";
        } else if (isStockAlto()) {
            this.estadoStock = "ALTO";
        } else {
            this.estadoStock = "NORMAL";
        }
    }
    
    private void calcularValorInventario() {
        if (cantidad != null && precioProducto != null) {
            this.valorInventario = precioProducto.multiply(BigDecimal.valueOf(cantidad));
        }
    }
    
    private void calcularPorcentajeStock() {
        if (cantidad != null && stockMinimo != null && stockMaximo != null && stockMaximo > stockMinimo) {
            double rango = stockMaximo - stockMinimo;
            double posicion = cantidad - stockMinimo;
            this.porcentajeStock = (posicion / rango) * 100.0;
        }
    }
    
    public String getValorInventarioFormateado() {
        return valorInventario != null ? "S/ " + valorInventario.toString() : "S/ 0.00";
    }
    
    public String getEstadoStockConEmoji() {
        String estado = getEstadoStock();
        switch (estado) {
            case "BAJO":
                return "⚠️ BAJO";
            case "ALTO":
                return "⬆️ ALTO";
            default:
                return "✅ NORMAL";
        }
    }
    
    @Override
    public String toString() {
        return "InventarioDTO{" +
                "idInventario=" + idInventario +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", descripcionAlmacen='" + descripcionAlmacen + '\'' +
                ", cantidad=" + cantidad +
                ", estadoStock='" + getEstadoStock() + '\'' +
                ", valorInventario=" + valorInventario +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        InventarioDTO that = (InventarioDTO) o;
        
        if (idInventario != null ? !idInventario.equals(that.idInventario) : that.idInventario != null) return false;
        if (idProducto != null ? !idProducto.equals(that.idProducto) : that.idProducto != null) return false;
        return idAlmacen != null ? idAlmacen.equals(that.idAlmacen) : that.idAlmacen == null;
    }
    
    @Override
    public int hashCode() {
        int result = idInventario != null ? idInventario.hashCode() : 0;
        result = 31 * result + (idProducto != null ? idProducto.hashCode() : 0);
        result = 31 * result + (idAlmacen != null ? idAlmacen.hashCode() : 0);
        return result;
    }
}