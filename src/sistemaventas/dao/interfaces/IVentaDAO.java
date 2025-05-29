/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dao.interfaces;

import sistemaventas.dao.base.IBaseDAO;
import sistemaventas.entity.Venta;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz específica para operaciones DAO de Venta
 */
public interface IVentaDAO extends IBaseDAO<Venta, Integer> {
    
    /**
     * Busca ventas por cliente
     * @param idCliente ID del cliente
     * @return Lista de ventas del cliente
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Venta> findByCliente(Integer idCliente) throws Exception;
    
    /**
     * Busca ventas por fecha específica
     * @param fecha Fecha de la venta
     * @return Lista de ventas en la fecha
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Venta> findByFecha(LocalDate fecha) throws Exception;
    
    /**
     * Busca ventas en un rango de fechas
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha fin del rango
     * @return Lista de ventas en el rango
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Venta> findByFechaRange(LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
    
    /**
     * Busca ventas por estado
     * @param estado Estado de la venta (PENDIENTE, COMPLETADA, CANCELADA)
     * @return Lista de ventas con el estado especificado
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Venta> findByEstado(Venta.EstadoVenta estado) throws Exception;
    
    /**
     * Obtiene las ventas del día actual
     * @return Lista de ventas de hoy
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Venta> findVentasHoy() throws Exception;
    
    /**
     * Obtiene las ventas del mes actual
     * @return Lista de ventas del mes
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Venta> findVentasMesActual() throws Exception;
    
    /**
     * Obtiene las ventas más recientes
     * @param limite Número máximo de ventas a retornar
     * @return Lista de ventas más recientes
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Venta> findVentasRecientes(int limite) throws Exception;
    
    /**
     * Calcula el total de ventas en un rango de fechas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha fin
     * @return Total de ventas en el período
     * @throws Exception Si ocurre un error en la consulta
     */
    java.math.BigDecimal calcularTotalVentas(LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
    
    /**
     * Cuenta las ventas por estado
     * @param estado Estado de la venta
     * @return Número de ventas con el estado
     * @throws Exception Si ocurre un error en la consulta
     */
    long countByEstado(Venta.EstadoVenta estado) throws Exception;
}