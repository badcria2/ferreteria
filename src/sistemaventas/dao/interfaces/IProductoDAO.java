/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dao.interfaces;

import sistemaventas.dao.base.IBaseDAO;
import sistemaventas.entity.Producto;
import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz específica para operaciones DAO de Producto
 */
public interface IProductoDAO extends IBaseDAO<Producto, Integer> {
    
    /**
     * Busca productos por nombre (búsqueda parcial)
     * @param nombre Nombre o parte del nombre del producto
     * @return Lista de productos que coinciden
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Producto> findByNombre(String nombre) throws Exception;
    
    /**
     * Busca productos en un rango de precios
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     * @return Lista de productos en el rango de precios
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Producto> findByPrecioRange(BigDecimal precioMin, BigDecimal precioMax) throws Exception;
    
    /**
     * Obtiene todos los productos activos
     * @return Lista de productos activos
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Producto> findActivos() throws Exception;
    
    /**
     * Busca productos más caros que el precio especificado
     * @param precio Precio de referencia
     * @return Lista de productos más caros
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Producto> findMasCarosQue(BigDecimal precio) throws Exception;
    
    /**
     * Busca productos más baratos que el precio especificado
     * @param precio Precio de referencia
     * @return Lista de productos más baratos
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Producto> findMasBaratosQue(BigDecimal precio) throws Exception;
    
    /**
     * Obtiene los productos más vendidos
     * @param limite Número máximo de productos a retornar
     * @return Lista de productos más vendidos
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Producto> findMasVendidos(int limite) throws Exception;
    
    /**
     * Cuenta el número de productos activos
     * @return Cantidad de productos activos
     * @throws Exception Si ocurre un error en la consulta
     */
    long countActivos() throws Exception;
}