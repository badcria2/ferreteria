/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.service.interfaces;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import sistemaventas.dto.DetalleVentaDTO;
import sistemaventas.dto.VentaDTO;

/**
 * Interfaz de servicio para la gestión de ventas
 * Define la lógica de negocio para operaciones con ventas
 */
public interface IVentaService {
    
    /**
     * Crea una nueva venta
     * @param ventaDTO Datos de la venta a crear
     * @return VentaDTO de la venta creada
     * @throws Exception Si ocurre un error en la creación o validación
     */
    VentaDTO crearVenta(VentaDTO ventaDTO) throws Exception;
    
    /**
     * Obtiene una venta por su ID
     * @param id ID de la venta
     * @return VentaDTO de la venta encontrada
     * @throws Exception Si la venta no existe o ocurre un error
     */
    VentaDTO obtenerVentaPorId(Integer id) throws Exception;
    
    /**
     * Lista todas las ventas
     * @return Lista de VentaDTO
     * @throws Exception Si ocurre un error en la consulta
     */
    List<VentaDTO> listarVentas() throws Exception;
    
    /**
     * Busca ventas por cliente
     * @param idCliente ID del cliente
     * @return Lista de VentaDTO del cliente
     * @throws Exception Si ocurre un error en la búsqueda
     */
    List<VentaDTO> buscarVentasPorCliente(Integer idCliente) throws Exception;
    
    /**
     * Busca ventas por fecha
     * @param fecha Fecha de la venta
     * @return Lista de VentaDTO en la fecha
     * @throws Exception Si ocurre un error en la búsqueda
     */
    List<VentaDTO> buscarVentasPorFecha(LocalDate fecha) throws Exception;
    
    /**
     * Busca ventas en un rango de fechas
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha fin del rango
     * @return Lista de VentaDTO en el rango
     * @throws Exception Si ocurre un error en la búsqueda
     */
    List<VentaDTO> buscarVentasPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
    
    /**
     * Busca ventas por estado
     * @param estado Estado de la venta (PENDIENTE, COMPLETADA, CANCELADA)
     * @return Lista de VentaDTO con el estado especificado
     * @throws Exception Si ocurre un error en la búsqueda
     */
    List<VentaDTO> buscarVentasPorEstado(String estado) throws Exception;
    
    /**
     * Actualiza una venta
     * @param ventaDTO Datos actualizados de la venta
     * @return VentaDTO de la venta actualizada
     * @throws Exception Si ocurre un error en la actualización
     */
    VentaDTO actualizarVenta(VentaDTO ventaDTO) throws Exception;
    
    /**
     * Cancela una venta
     * @param id ID de la venta a cancelar
     * @return true si se canceló correctamente
     * @throws Exception Si ocurre un error en la cancelación
     */
    boolean cancelarVenta(Integer id) throws Exception;
    
    /**
     * Completa una venta
     * @param id ID de la venta a completar
     * @return VentaDTO de la venta completada
     * @throws Exception Si ocurre un error en la operación
     */
    VentaDTO completarVenta(Integer id) throws Exception;
    
    /**
     * Agrega un detalle a una venta
     * @param idVenta ID de la venta
     * @param detalleVentaDTO Detalle a agregar
     * @return DetalleVentaDTO agregado
     * @throws Exception Si ocurre un error en la operación
     */
    DetalleVentaDTO agregarDetalleVenta(Integer idVenta, DetalleVentaDTO detalleVentaDTO) throws Exception;
    
    /**
     * Elimina un detalle de una venta
     * @param idVenta ID de la venta
     * @param idDetalleVenta ID del detalle a eliminar
     * @return true si se eliminó correctamente
     * @throws Exception Si ocurre un error en la operación
     */
    boolean eliminarDetalleVenta(Integer idVenta, Integer idDetalleVenta) throws Exception;
    
    /**
     * Obtiene los detalles de una venta
     * @param idVenta ID de la venta
     * @return Lista de DetalleVentaDTO de la venta
     * @throws Exception Si ocurre un error en la consulta
     */
    List<DetalleVentaDTO> obtenerDetallesVenta(Integer idVenta) throws Exception;
    
    /**
     * Calcula el total de ventas en un período
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha fin del período
     * @return Total de ventas en el período
     * @throws Exception Si ocurre un error en el cálculo
     */
    BigDecimal calcularTotalVentas(LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
    
    /**
     * Obtiene las ventas del día actual
     * @return Lista de VentaDTO de hoy
     * @throws Exception Si ocurre un error en la consulta
     */
    List<VentaDTO> obtenerVentasHoy() throws Exception;
    
    /**
     * Obtiene las ventas del mes actual
     * @return Lista de VentaDTO del mes
     * @throws Exception Si ocurre un error en la consulta
     */
    List<VentaDTO> obtenerVentasMesActual() throws Exception;
    
    /**
     * Obtiene las ventas más recientes
     * @param limite Número máximo de ventas a retornar
     * @return Lista de VentaDTO más recientes
     * @throws Exception Si ocurre un error en la consulta
     */
    List<VentaDTO> obtenerVentasRecientes(int limite) throws Exception;
    
    /**
     * Valida si una venta puede ser modificada
     * @param id ID de la venta
     * @return true si puede ser modificada
     * @throws Exception Si ocurre un error en la validación
     */
    boolean puedeModificarVenta(Integer id) throws Exception;
    
    /**
     * Obtiene estadísticas de ventas
     * @return Estadísticas generales de ventas
     * @throws Exception Si ocurre un error en el cálculo
     */
    Object obtenerEstadisticasVentas() throws Exception;
}