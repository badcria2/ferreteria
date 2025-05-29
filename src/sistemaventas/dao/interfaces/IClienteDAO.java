/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dao.interfaces;

import sistemaventas.dao.base.IBaseDAO;
import sistemaventas.entity.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz específica para operaciones DAO de Cliente
 * Extiende la interfaz base y añade métodos específicos
 */
public interface IClienteDAO extends IBaseDAO<Cliente, Integer> {
    
    /**
     * Busca un cliente por su número de documento
     * @param nroDocumento Número de documento del cliente
     * @return Optional con el cliente si existe
     * @throws Exception Si ocurre un error en la consulta
     */
    Optional<Cliente> findByDocumento(String nroDocumento) throws Exception;
    
    /**
     * Busca clientes por nombre (búsqueda parcial)
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de clientes que coinciden
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Cliente> findByNombre(String nombre) throws Exception;
    
    /**
     * Obtiene todos los clientes activos
     * @return Lista de clientes activos
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Cliente> findActivos() throws Exception;
    
    /**
     * Busca clientes por correo electrónico
     * @param correo Correo electrónico del cliente
     * @return Optional con el cliente si existe
     * @throws Exception Si ocurre un error en la consulta
     */
    Optional<Cliente> findByCorreo(String correo) throws Exception;
    
    /**
     * Cuenta el número de clientes activos
     * @return Cantidad de clientes activos
     * @throws Exception Si ocurre un error en la consulta
     */
    long countActivos() throws Exception;
    
    /**
     * Busca clientes registrados en un rango de fechas
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha fin del rango
     * @return Lista de clientes registrados en el rango
     * @throws Exception Si ocurre un error en la consulta
     */
    List<Cliente> findByFechaRegistro(java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin) throws Exception;
}