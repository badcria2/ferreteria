/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dao.interfaces;

import sistemaventas.dao.base.IBaseDAO;
import sistemaventas.entity.Almacen;
import java.util.List;

/**
 * Interfaz específica para operaciones DAO de Almacén
 */
public interface IAlmacenDAO extends IBaseDAO<Almacen, Integer> {
    
    /**
     * Busca almacenes por descripción (búsqueda parcial)
     * @param descripcion Descripción o parte de la descripción
     * @return Lista de almacenes que coinciden
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Almacen> findByDescripcion(String descripcion) throws Exception;
    
    /**
     * Obtiene todos los almacenes activos
     * @return Lista de almacenes activos
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Almacen> findActivos() throws Exception;
    
    /**
     * Busca almacenes por ciudad/ubicación
     * @param ubicacion Ubicación del almacén
     * @return Lista de almacenes en la ubicación
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Almacen> findByUbicacion(String ubicacion) throws Exception;
    
    /**
     * Busca almacenes por teléfono
     * @param telefono Teléfono del almacén
     * @return Lista de almacenes con el teléfono
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Almacen> findByTelefono(String telefono) throws Exception;
    
    /**
     * Cuenta el número de almacenes activos
     * @return Cantidad de almacenes activos
     * @throws Exception Si ocurre un error en la consulta
     */
    long countActivos() throws Exception;
}