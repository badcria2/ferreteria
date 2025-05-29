/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.service.interfaces; 
import java.util.List;
import sistemaventas.dto.InventarioDTO;

/**
 * Interfaz de servicio para la gestión de inventario
 * Define la lógica de negocio para operaciones con inventario
 */
public interface IInventarioService {
    
    /**
     * Crea un nuevo registro de inventario
     * @param inventarioDTO Datos del inventario a crear
     * @return InventarioDTO del inventario creado
     * @throws Exception Si ocurre un error en la creación o validación
     */
    InventarioDTO crearInventario(InventarioDTO inventarioDTO) throws Exception;
    
    /**
     * Obtiene un inventario por su ID
     * @param id ID del inventario
     * @return InventarioDTO del inventario encontrado
     * @throws Exception Si el inventario no existe o ocurre un error
     */
    InventarioDTO obtenerInventarioPorId(Integer id) throws Exception;
    
    /**
     * Lista todos los inventarios activos
     * @return Lista de InventarioDTO
     * @throws Exception Si ocurre un error en la consulta
     */
    List<InventarioDTO> listarInventarios() throws Exception;
    
    /**
     * Obtiene inventarios por almacén
     * @param idAlmacen ID del almacén
     * @return Lista de InventarioDTO del almacén
     * @throws Exception Si ocurre un error en la consulta
     */
    List<InventarioDTO> obtenerInventarioPorAlmacen(Integer idAlmacen) throws Exception;
    
    /**
     * Obtiene inventarios por producto
     * @param idProducto ID del producto
     * @return Lista de InventarioDTO del producto
     * @throws Exception Si ocurre un error en la consulta
     */
    List<InventarioDTO> obtenerInventarioPorProducto(Integer idProducto) throws Exception;
    
    /**
     * Obtiene inventarios con stock bajo
     * @return Lista de InventarioDTO con stock bajo
     * @throws Exception Si ocurre un error en la consulta
     */
    List<InventarioDTO> obtenerStockBajo() throws Exception;
    
    /**
     * Obtiene inventarios con stock alto
     * @return Lista de InventarioDTO con stock alto
     * @throws Exception Si ocurre un error en la consulta
     */
    List<InventarioDTO> obtenerStockAlto() throws Exception;
    
    /**
     * Obtiene inventarios con stock normal
     * @return Lista de InventarioDTO con stock normal
     * @throws Exception Si ocurre un error en la consulta
     */
    List<InventarioDTO> obtenerStockNormal() throws Exception;
    
    /**
     * Verifica si hay stock disponible para una cantidad específica
     * @param idInventario ID del inventario
     * @param cantidad Cantidad solicitada
     * @return true si hay stock suficiente
     * @throws Exception Si ocurre un error en la verificación
     */
    boolean verificarStockDisponible(Integer idInventario, Integer cantidad) throws Exception;
    
    /**
     * Actualiza el stock de un inventario
     * @param idInventario ID del inventario
     * @param cantidad Nueva cantidad
     * @return InventarioDTO actualizado
     * @throws Exception Si ocurre un error en la actualización
     */
    InventarioDTO actualizarStock(Integer idInventario, Integer cantidad) throws Exception;
    
    /**
     * Aumenta el stock de un inventario
     * @param idInventario ID del inventario
     * @param cantidad Cantidad a aumentar
     * @return InventarioDTO actualizado
     * @throws Exception Si ocurre un error en la operación
     */
    InventarioDTO aumentarStock(Integer idInventario, Integer cantidad) throws Exception;
    
    /**
     * Disminuye el stock de un inventario
     * @param idInventario ID del inventario
     * @param cantidad Cantidad a disminuir
     * @return InventarioDTO actualizado
     * @throws Exception Si ocurre un error en la operación
     */
    InventarioDTO disminuirStock(Integer idInventario, Integer cantidad) throws Exception;
    
    /**
     * Busca inventario específico por producto y almacén
     * @param idProducto ID del producto
     * @param idAlmacen ID del almacén
     * @return InventarioDTO encontrado
     * @throws Exception Si no existe o ocurre un error
     */
    InventarioDTO obtenerInventarioPorProductoYAlmacen(Integer idProducto, Integer idAlmacen) throws Exception;
    
    /**
     * Actualiza los límites de stock de un inventario
     * @param idInventario ID del inventario
     * @param stockMinimo Nuevo stock mínimo
     * @param stockMaximo Nuevo stock máximo
     * @return InventarioDTO actualizado
     * @throws Exception Si ocurre un error en la actualización
     */
    InventarioDTO actualizarLimitesStock(Integer idInventario, Integer stockMinimo, Integer stockMaximo) throws Exception;
    
    /**
     * Obtiene estadísticas generales del inventario
     * @return Estadísticas del inventario
     * @throws Exception Si ocurre un error en el cálculo
     */
    Object obtenerEstadisticasInventario() throws Exception;
    
    /**
     * Transfiere stock entre almacenes
     * @param idProducto ID del producto
     * @param idAlmacenOrigen ID del almacén origen
     * @param idAlmacenDestino ID del almacén destino
     * @param cantidad Cantidad a transferir
     * @return true si la transferencia fue exitosa
     * @throws Exception Si ocurre un error en la transferencia
     */
    boolean transferirStock(Integer idProducto, Integer idAlmacenOrigen, Integer idAlmacenDestino, Integer cantidad) throws Exception;
}
