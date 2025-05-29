/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dao.interfaces;

import sistemaventas.dao.base.IBaseDAO;
import sistemaventas.entity.Inventario;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz específica para operaciones DAO de Inventario
 */
public interface IInventarioDAO extends IBaseDAO<Inventario, Integer> {
    
    /**
     * Busca inventarios por almacén
     * @param idAlmacen ID del almacén
     * @return Lista de inventarios del almacén
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Inventario> findByAlmacen(Integer idAlmacen) throws Exception;
    
    /**
     * Busca inventarios por producto
     * @param idProducto ID del producto
     * @return Lista de inventarios del producto
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Inventario> findByProducto(Integer idProducto) throws Exception;
    
    /**
     * Busca inventario específico por producto y almacén
     * @param idProducto ID del producto
     * @param idAlmacen ID del almacén
     * @return Optional con el inventario si existe
     * @throws Exception Si ocurre un error en la consulta
     */
    Optional<Inventario> findByProductoAndAlmacen(Integer idProducto, Integer idAlmacen) throws Exception;
    
    /**
     * Busca inventarios con stock bajo (cantidad <= stock mínimo)
     * @return Lista de inventarios con stock bajo
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Inventario> findStockBajo() throws Exception;
    
    /**
     * Busca inventarios con stock alto (cantidad >= stock máximo)
     * @return Lista de inventarios con stock alto
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Inventario> findStockAlto() throws Exception;
    
    /**
     * Busca inventarios con stock normal (entre mínimo y máximo)
     * @return Lista de inventarios con stock normal
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Inventario> findStockNormal() throws Exception;
    
    /**
     * Actualiza el stock de un inventario específico
     * @param idInventario ID del inventario
     * @param cantidad Nueva cantidad
     * @return true si se actualizó correctamente
     * @throws Exception Si ocurre un error en la actualización
     */
    boolean actualizarStock(Integer idInventario, Integer cantidad) throws Exception;
    
    /**
     * Reduce el stock de un inventario (solo si hay suficiente stock)
     * @param idInventario ID del inventario
     * @param cantidad Cantidad a reducir
     * @return true si se redujo correctamente
     * @throws Exception Si ocurre un error en la operación
     */
    boolean reducirStock(Integer idInventario, Integer cantidad) throws Exception;
    
    /**
     * Aumenta el stock de un inventario
     * @param idInventario ID del inventario
     * @param cantidad Cantidad a aumentar
     * @return true si se aumentó correctamente
     * @throws Exception Si ocurre un error en la operación
     */
    boolean aumentarStock(Integer idInventario, Integer cantidad) throws Exception;
    
    /**
     * Obtiene el stock disponible de un inventario
     * @param idInventario ID del inventario
     * @return Optional con la cantidad disponible
     * @throws Exception Si ocurre un error en la consulta
     */
    Optional<Integer> getStockDisponible(Integer idInventario) throws Exception;
}