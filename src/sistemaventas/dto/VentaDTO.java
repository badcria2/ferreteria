/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para transferencia de datos de Venta
 * Incluye información del Cliente y Detalles de venta
 */
public class VentaDTO {
    private Integer idVentas;
    private LocalDate fecha;
    private Integer idCliente;
    private BigDecimal total;
    private String estado; // PENDIENTE, COMPLETADA, CANCELADA
    private LocalDateTime fechaRegistro;
    
    // Información del Cliente relacionado
    private String nombreCliente;
    private String documentoCliente;
    private String correoCliente;
    
    // Información de los detalles
    private List<DetalleVentaDTO> detalles;
    private Integer totalItems;
    private Integer totalCantidad;
    
    // Campos calculados para presentación
    private String totalFormateado;
    private String estadoDescripcion;
    
    // Constructores
    public VentaDTO() {}
    
    public VentaDTO(Integer idCliente, LocalDate fecha) {
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.total = BigDecimal.ZERO;
        this.estado = "PENDIENTE";
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public VentaDTO(Integer idVentas, Integer idCliente, LocalDate fecha, BigDecimal total, String estado) {
        this.idVentas = idVentas;
        this.idCliente = idCliente;
        this.fecha = fecha;
        this.total = total;
        this.estado = estado;
    }
    
    // Getters y Setters
    public Integer getIdVentas() {
        return idVentas;
    }
    
    public void setIdVentas(Integer idVentas) {
        this.idVentas = idVentas;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public Integer getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
        actualizarEstadoDescripcion();
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    public String getDocumentoCliente() {
        return documentoCliente;
    }
    
    public void setDocumentoCliente(String documentoCliente) {
        this.documentoCliente = documentoCliente;
    }
    
    public String getCorreoCliente() {
        return correoCliente;
    }
    
    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }
    
    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleVentaDTO> detalles) {
        this.detalles = detalles;
        calcularTotales();
    }
    
    public Integer getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
    
    public Integer getTotalCantidad() {
        return totalCantidad;
    }
    
    public void setTotalCantidad(Integer totalCantidad) {
        this.totalCantidad = totalCantidad;
    }
    
    public String getTotalFormateado() {
        return total != null ? "S/ " + total.toString() : "S/ 0.00";
    }
    
    public void setTotalFormateado(String totalFormateado) {
        this.totalFormateado = totalFormateado;
    }
    
    public String getEstadoDescripcion() {
        if (estadoDescripcion == null) {
            actualizarEstadoDescripcion();
        }
        return estadoDescripcion;
    }
    
    public void setEstadoDescripcion(String estadoDescripcion) {
        this.estadoDescripcion = estadoDescripcion;
    }
    
    // Métodos de utilidad
    public boolean isPendiente() {
        return "PENDIENTE".equals(estado);
    }
    
    public boolean isCompletada() {
        return "COMPLETADA".equals(estado);
    }
    
    public boolean isCancelada() {
        return "CANCELADA".equals(estado);
    }
    
    public boolean puedeSerCancelada() {
        return isPendiente();
    }
    
    public boolean puedeSerCompletada() {
        return isPendiente();
    }
    
    private void calcularTotales() {
        if (detalles != null && !detalles.isEmpty()) {
            this.totalItems = detalles.size();
            this.totalCantidad = detalles.stream()
                    .mapToInt(DetalleVentaDTO::getCantidad)
                    .sum();
            this.total = detalles.stream()
                    .map(DetalleVentaDTO::getPreciototal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
    
    private void actualizarEstadoDescripcion() {
        if (estado != null) {
            switch (estado) {
                case "PENDIENTE":
                    this.estadoDescripcion = "⏳ Pendiente";
                    break;
                case "COMPLETADA":
                    this.estadoDescripcion = "✅ Completada";
                    break;
                case "CANCELADA":
                    this.estadoDescripcion = "❌ Cancelada";
                    break;
                default:
                    this.estadoDescripcion = estado;
            }
        }
    }
    
    @Override
    public String toString() {
        return "VentaDTO{" +
                "idVentas=" + idVentas +
                ", fecha=" + fecha +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                ", totalItems=" + totalItems +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        VentaDTO ventaDTO = (VentaDTO) o;
        
        return idVentas != null ? idVentas.equals(ventaDTO.idVentas) : ventaDTO.idVentas == null;
    }
    
    @Override
    public int hashCode() {
        return idVentas != null ? idVentas.hashCode() : 0;
    }
}
