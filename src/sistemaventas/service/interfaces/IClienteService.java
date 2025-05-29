/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.service.interfaces;

import java.time.LocalDate;
import java.util.List;
import sistemaventas.dto.ClienteDTO;

/**
 * Interfaz de servicio para la gestión de clientes
 * Define la lógica de negocio para operaciones con clientes
 */
public interface IClienteService {
    
    /**
     * Crea un nuevo cliente
     * @param clienteDTO Datos del cliente a crear
     * @return ClienteDTO del cliente creado
     * @throws Exception Si ocurre un error en la creación o validación
     */
    ClienteDTO crearCliente(ClienteDTO clienteDTO) throws Exception;
    
    /**
     * Obtiene un cliente por su ID
     * @param id ID del cliente
     * @return ClienteDTO del cliente encontrado
     * @throws Exception Si el cliente no existe o ocurre un error
     */
    ClienteDTO obtenerClientePorId(Integer id) throws Exception;
    
    /**
     * Obtiene un cliente por su número de documento
     * @param documento Número de documento del cliente
     * @return ClienteDTO del cliente encontrado
     * @throws Exception Si el cliente no existe o ocurre un error
     */
    ClienteDTO obtenerClientePorDocumento(String documento) throws Exception;
    
    /**
     * Lista todos los clientes activos
     * @return Lista de ClienteDTO
     * @throws Exception Si ocurre un error en la consulta
     */
    List<ClienteDTO> listarClientes() throws Exception;
    
    /**
     * Busca clientes por nombre (búsqueda parcial)
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de ClienteDTO que coinciden
     * @throws Exception Si ocurre un error en la búsqueda
     */
    List<ClienteDTO> buscarClientesPorNombre(String nombre) throws Exception;
    
    /**
     * Actualiza los datos de un cliente
     * @param clienteDTO Datos actualizados del cliente
     * @return ClienteDTO del cliente actualizado
     * @throws Exception Si ocurre un error en la actualización o validación
     */
    ClienteDTO actualizarCliente(ClienteDTO clienteDTO) throws Exception;
    
    /**
     * Elimina un cliente (eliminación lógica)
     * @param id ID del cliente a eliminar
     * @return true si se eliminó correctamente
     * @throws Exception Si ocurre un error en la eliminación
     */
    boolean eliminarCliente(Integer id) throws Exception;
    
    /**
     * Valida si un documento es único en el sistema
     * @param documento Número de documento a validar
     * @return true si el documento es único
     * @throws Exception Si ocurre un error en la validación
     */
    boolean validarDocumentoUnico(String documento) throws Exception;
    
    /**
     * Valida si un documento es único excluyendo un cliente específico
     * @param documento Número de documento a validar
     * @param excludeId ID del cliente a excluir de la validación
     * @return true si el documento es único
     * @throws Exception Si ocurre un error en la validación
     */
    boolean validarDocumentoUnico(String documento, Integer excludeId) throws Exception;
    
    /**
     * Busca clientes por correo electrónico
     * @param correo Correo electrónico del cliente
     * @return ClienteDTO del cliente encontrado
     * @throws Exception Si el cliente no existe o ocurre un error
     */
    ClienteDTO obtenerClientePorCorreo(String correo) throws Exception;
    
    /**
     * Obtiene clientes registrados en un rango de fechas
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha fin del rango
     * @return Lista de ClienteDTO registrados en el período
     * @throws Exception Si ocurre un error en la consulta
     */
    List<ClienteDTO> obtenerClientesPorFechaRegistro(LocalDate fechaInicio, LocalDate fechaFin) throws Exception;
    
    /**
     * Cuenta el total de clientes activos
     * @return Número de clientes activos
     * @throws Exception Si ocurre un error en el conteo
     */
    long contarClientesActivos() throws Exception;
    
    /**
     * Obtiene los clientes más recientes
     * @param limite Número máximo de clientes a retornar
     * @return Lista de ClienteDTO más recientes
     * @throws Exception Si ocurre un error en la consulta
     */
    List<ClienteDTO> obtenerClientesRecientes(int limite) throws Exception;
}