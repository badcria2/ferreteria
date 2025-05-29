/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.service.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import sistemaventas.dto.ProductoDTO;

/**
 * Interfaz de servicio para la gestión de productos
 * Define la lógica de negocio para operaciones con productos
 */
public interface IProductoService {
    
    /**
     * Crea un nuevo producto
     * @param productoDTO Datos del producto a crear
     * @return ProductoDTO del producto creado
     * @throws Exception Si ocurre un error en la creación o validación
     */
    ProductoDTO crearProducto(ProductoDTO productoDTO) throws Exception;
    
    /**
     * Obtiene un producto por su ID
     * @param id ID del producto
     * @return ProductoDTO del producto encontrado
     * @throws Exception Si el producto no existe o ocurre un error
     */
    ProductoDTO obtenerProductoPorId(Integer id) throws Exception;
    
    /**
     * Lista todos los productos activos
     * @return Lista de ProductoDTO
     * @throws Exception Si ocurre un error en la consulta
     */
    List<ProductoDTO> listarProductos() throws Exception;
    
    /**
     * Busca productos por nombre (búsqueda parcial)
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de ProductoDTO que coinciden
     * @throws Exception Si ocurre un error en la búsqueda
     */
    List<ProductoDTO> buscarProductosPorNombre(String nombre) throws Exception;
    
    /**
     * Actualiza los datos de un producto
     * @param productoDTO Datos actualizados del producto
     * @return ProductoDTO del producto actualizado
     * @throws Exception Si ocurre un error en la actualización o validación
     */
    ProductoDTO actualizarProducto(ProductoDTO productoDTO) throws Exception;
    
    /**
     * Elimina un producto (eliminación lógica)
     * @param id ID del producto a eliminar
     * @return true si se eliminó correctamente
     * @throws Exception Si ocurre un error en la eliminación
     */
    boolean eliminarProducto(Integer id) throws Exception;
    
    /**
     * Busca productos en un rango de precios
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     * @return Lista de ProductoDTO en el rango de precios
     * @throws Exception Si ocurre un error en la búsqueda
     */
    List<ProductoDTO> buscarProductosPorPrecio(BigDecimal precioMin, BigDecimal precioMax) throws Exception;
    
    /**
     * Obtiene los productos más vendidos
     * @param limite Número máximo de productos a retornar
     * @return Lista de ProductoDTO más vendidos
     * @throws Exception Si ocurre un error en la consulta
     */
    List<ProductoDTO> obtenerProductosMasVendidos(int limite) throws Exception;
    
    /**
     * Obtiene productos con stock disponible
     * @return Lista de ProductoDTO con stock
     * @throws Exception Si ocurre un error en la consulta
     */
    List<ProductoDTO> obtenerProductosConStock() throws Exception;
    
    /**
     * Obtiene productos sin inventario
     * @return Lista de ProductoDTO sin stock en ningún almacén
     * @throws Exception Si ocurre un error en la consulta
     */
    List<ProductoDTO> obtenerProductosSinInventario() throws Exception;
    
    /**
     * Actualiza solo el precio de un producto
     * @param id ID del producto
     * @param nuevoPrecio Nuevo precio del producto
     * @return ProductoDTO del producto actualizado
     * @throws Exception Si ocurre un error en la actualización
     */
    ProductoDTO actualizarPrecioProducto(Integer id, BigDecimal nuevoPrecio) throws Exception;
    
    /**
     * Valida si el nombre del producto es único
     * @param nombre Nombre del producto
     * @param excludeId ID del producto a excluir (para actualizaciones)
     * @return true si el nombre es único
     * @throws Exception Si ocurre un error en la validación
     */
    boolean validarNombreUnico(String nombre, Integer excludeId) throws Exception;
    
    /**
     * Obtiene estadísticas de productos
     * @return Estadísticas generales de productos
     * @throws Exception Si ocurre un error en el cálculo
     */
    Object obtenerEstadisticasProductos() throws Exception;
    
    /**
     * Busca productos por descripción
     * @param descripcion Descripción o parte de la descripción
     * @return Lista de ProductoDTO que coinciden
     * @throws Exception Si ocurre un error en la búsqueda
     */
    List<ProductoDTO> buscarProductosPorDescripcion(String descripcion) throws Exception;
    
    /**
     * Cuenta el total de productos activos
     * @return Número de productos activos
     * @throws Exception Si ocurre un error en el conteo
     */
    long contarProductosActivos() throws Exception;
}
