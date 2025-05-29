/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dao.interfaces;

import sistemaventas.dao.base.IBaseDAO;
import sistemaventas.entity.DetalleVenta;
import java.util.List;

/**
 * Interfaz específica para operaciones DAO de Detalle de Venta
 */
public interface IDetalleVentaDAO extends IBaseDAO<DetalleVenta, Integer> {
    
    /**
     * Busca detalles de venta por venta
     * @param idVenta ID de la venta
     * @return Lista de detalles de la venta
     * @throws Exception Si ocurre un error en la consulta
     */
    List<DetalleVenta> findByVenta(Integer idVenta) throws Exception;
    
    /**
     * Busca detalles de venta por inventario
     * @param idInventario ID del inventario
     * @return Lista de detalles que incluyen el inventario
     * @throws Exception Si ocurre un error en la consulta
     */
    List<DetalleVenta> findByInventario(Integer idInventario) throws Exception;
    
    /**
     * Busca detalles de venta por producto
     * @param idProducto ID del producto
     * @return Lista de detalles que incluyen el producto
     * @throws Exception Si ocurre un error en la consulta
     */
    List<DetalleVenta> findByProducto(Integer idProducto) throws Exception;
    
    /**
     * Calcula el total vendido de un producto en un período
     * @param idProducto ID del producto
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha fin del período
     * @return Cantidad total vendida del producto
     * @throws Exception Si ocurre un error en la consulta
     */
    Integer calcularCantidadVendida(Integer idProducto, java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin) throws Exception;
    
    /**
     * Obtiene los productos más vendidos
     * @param limite Número máximo de productos a retornar
     * @return Lista de detalles de productos más vendidos
     * @throws Exception Si ocurre un error en la consulta
     */
    List<DetalleVenta> findProductosMasVendidos(int limite) throws Exception;
    
    /**
     * Elimina todos los detalles de una venta
     * @param idVenta ID de la venta
     * @return true si se eliminaron correctamente
     * @throws Exception Si ocurre un error en la operación
     */
    boolean deleteByVenta(Integer idVenta) throws Exception;
    
    /**
     * Cuenta el número de items en una venta
     * @param idVenta ID de la venta
     * @return Número de items diferentes en la venta
     * @throws Exception Si ocurre un error en la consulta
     */
    long countItemsByVenta(Integer idVenta) throws Exception;
    
    /**
     * Calcula la cantidad total de productos en una venta
     * @param idVenta ID de la venta
     * @return Cantidad total de productos
     * @throws Exception Si ocurre un error en la consulta
     */
    Integer calcularCantidadTotalByVenta(Integer idVenta) throws Exception;
}