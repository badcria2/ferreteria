/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.service.impl;

import sistemaventas.dao.interfaces.IProductoDAO;
import sistemaventas.dao.impl.ProductoDAOImpl;
import sistemaventas.dto.ProductoDTO;
import sistemaventas.entity.Producto;
import sistemaventas.exception.BusinessException;
import sistemaventas.exception.ValidationException;
import sistemaventas.service.interfaces.IProductoService;
import sistemaventas.util.ValidationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de productos
 * Contiene la lógica de negocio para la gestión de productos
 */
public class ProductoServiceImpl implements IProductoService {
    
    private final IProductoDAO productoDAO;
    
    public ProductoServiceImpl() {
        this.productoDAO = new ProductoDAOImpl();
    }
    
    // Constructor para inyección de dependencias (testing)
    public ProductoServiceImpl(IProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }
    
    @Override
    public ProductoDTO crearProducto(ProductoDTO productoDTO) throws Exception {
        // Validaciones de negocio
        validarDatosProducto(productoDTO);
        
        // Verificar nombre único
        if (!validarNombreUnico(productoDTO.getNombreproducto(), null)) {
            throw new BusinessException("Ya existe un producto con este nombre");
        }
        
        // Convertir DTO a Entity
        Producto producto = convertirDTOAEntity(productoDTO);
        
        // Crear producto
        Producto productoCreado = productoDAO.create(producto);
        
        // Convertir Entity a DTO y retornar
        return convertirEntityADTO(productoCreado);
    }
    
    @Override
    public ProductoDTO obtenerProductoPorId(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new ValidationException("ID de producto inválido");
        }
        
        Optional<Producto> producto = productoDAO.findById(id);
        if (producto.isEmpty()) {
            throw new BusinessException("Producto no encontrado con ID: " + id);
        }
        
        return convertirEntityADTO(producto.get());
    }
    
    @Override
    public List<ProductoDTO> listarProductos() throws Exception {
        List<Producto> productos = productoDAO.findAll();
        return convertirListaEntityADTO(productos);
    }
    
    @Override
    public List<ProductoDTO> buscarProductosPorNombre(String nombre) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("Nombre no puede estar vacío");
        }
        
        List<Producto> productos = productoDAO.findByNombre(nombre.trim());
        return convertirListaEntityADTO(productos);
    }
    
    @Override
    public ProductoDTO actualizarProducto(ProductoDTO productoDTO) throws Exception {
        if (productoDTO.getIdProducto() == null) {
            throw new ValidationException("ID de producto requerido para actualización");
        }
        
        // Validar que el producto existe
        obtenerProductoPorId(productoDTO.getIdProducto());
        
        // Validar datos
        validarDatosProducto(productoDTO);
        
        // Verificar nombre único (excluyendo el producto actual)
        if (!validarNombreUnico(productoDTO.getNombreproducto(), productoDTO.getIdProducto())) {
            throw new BusinessException("Ya existe otro producto con este nombre");
        }
        
        // Convertir y actualizar
        Producto producto = convertirDTOAEntity(productoDTO);
        Producto productoActualizado = productoDAO.update(producto);
        
        return convertirEntityADTO(productoActualizado);
    }
    
    @Override
    public boolean eliminarProducto(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new ValidationException("ID de producto inválido");
        }
        
        // Verificar que el producto existe
        obtenerProductoPorId(id);
        
        // TODO: Verificar que no tenga ventas pendientes o inventario
        
        return productoDAO.delete(id);
    }
    
    @Override
    public List<ProductoDTO> buscarProductosPorPrecio(BigDecimal precioMin, BigDecimal precioMax) throws Exception {
        if (precioMin == null || precioMax == null) {
            throw new ValidationException("Los precios mínimo y máximo son requeridos");
        }
        
        if (precioMin.compareTo(BigDecimal.ZERO) < 0 || precioMax.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Los precios no pueden ser negativos");
        }
        
        if (precioMin.compareTo(precioMax) > 0) {
            throw new ValidationException("El precio mínimo no puede ser mayor al precio máximo");
        }
        
        List<Producto> productos = productoDAO.findByPrecioRange(precioMin, precioMax);
        return convertirListaEntityADTO(productos);
    }
    
    @Override
    public List<ProductoDTO> obtenerProductosMasVendidos(int limite) throws Exception {
        if (limite <= 0) {
            throw new ValidationException("El límite debe ser mayor a cero");
        }
        
        List<Producto> productos = productoDAO.findMasVendidos(limite);
        return convertirListaEntityADTO(productos);
    }
    
    @Override
    public List<ProductoDTO> obtenerProductosConStock() throws Exception {
        ProductoDAOImpl productoDAOImpl = (ProductoDAOImpl) productoDAO;
        List<Producto> productos = productoDAOImpl.findConStock();
        return convertirListaEntityADTO(productos);
    }
    
    @Override
    public List<ProductoDTO> obtenerProductosSinInventario() throws Exception {
        ProductoDAOImpl productoDAOImpl = (ProductoDAOImpl) productoDAO;
        List<Producto> productos = productoDAOImpl.findSinInventario();
        return convertirListaEntityADTO(productos);
    }
    
    @Override
    public ProductoDTO actualizarPrecioProducto(Integer id, BigDecimal nuevoPrecio) throws Exception {
        if (id == null || id <= 0) {
            throw new ValidationException("ID de producto inválido");
        }
        
        if (nuevoPrecio == null || nuevoPrecio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("El nuevo precio debe ser mayor a cero");
        }
        
        // Verificar que el producto existe
        ProductoDTO productoExistente = obtenerProductoPorId(id);
        
        // Actualizar precio
        ProductoDAOImpl productoDAOImpl = (ProductoDAOImpl) productoDAO;
        boolean actualizado = productoDAOImpl.actualizarPrecio(id, nuevoPrecio);
        
        if (!actualizado) {
            throw new BusinessException("No se pudo actualizar el precio del producto");
        }
        
        // Retornar producto actualizado
        return obtenerProductoPorId(id);
    }
    
    @Override
    public boolean validarNombreUnico(String nombre, Integer excludeId) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        
        ProductoDAOImpl productoDAOImpl = (ProductoDAOImpl) productoDAO;
        return !productoDAOImpl.existeNombreProducto(nombre.trim(), excludeId);
    }
    
    @Override
    public Object obtenerEstadisticasProductos() throws Exception {
        ProductoDAOImpl productoDAOImpl = (ProductoDAOImpl) productoDAO;
        return productoDAOImpl.getEstadisticasProductos();
    }
    
    @Override
    public List<ProductoDTO> buscarProductosPorDescripcion(String descripcion) throws Exception {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new ValidationException("Descripción no puede estar vacía");
        }
        
        ProductoDAOImpl productoDAOImpl = (ProductoDAOImpl) productoDAO;
        List<Producto> productos = productoDAOImpl.findByDescripcion(descripcion.trim());
        return convertirListaEntityADTO(productos);
    }
    
    @Override
    public long contarProductosActivos() throws Exception {
        return productoDAO.countActivos();
    }
    
    // ============================================
    // MÉTODOS PRIVADOS DE VALIDACIÓN Y CONVERSIÓN
    // ============================================
    
    private void validarDatosProducto(ProductoDTO productoDTO) throws ValidationException {
        if (productoDTO == null) {
            throw new ValidationException("Datos del producto requeridos");
        }
        
        if (productoDTO.getNombreproducto() == null || productoDTO.getNombreproducto().trim().isEmpty()) {
            throw new ValidationException("Nombre del producto es requerido");
        }
        
        if (productoDTO.getNombreproducto().trim().length() < 2 || productoDTO.getNombreproducto().trim().length() > 45) {
            throw new ValidationException("El nombre del producto debe tener entre 2 y 45 caracteres");
        }
        
        if (productoDTO.getPrecio() == null || productoDTO.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("El precio debe ser mayor a cero");
        }
        
        if (productoDTO.getPrecio().compareTo(new BigDecimal("999999.99")) > 0) {
            throw new ValidationException("El precio no puede exceder 999,999.99");
        }
        
        if (productoDTO.getDescripcion() != null && productoDTO.getDescripcion().length() > 500) {
            throw new ValidationException("La descripción no puede exceder 500 caracteres");
        }
    }
    
    private Producto convertirDTOAEntity(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setIdProducto(dto.getIdProducto());
        producto.setNombreproducto(ValidationUtil.sanitizeString(dto.getNombreproducto()));
        producto.setPrecio(dto.getPrecio());
        producto.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : null);
        producto.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return producto;
    }
    
    private ProductoDTO convertirEntityADTO(Producto entity) {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(entity.getIdProducto());
        dto.setNombreproducto(entity.getNombreproducto());
        dto.setPrecio(entity.getPrecio());
        dto.setDescripcion(entity.getDescripcion());
        dto.setFechaRegistro(entity.getFechaRegistro());
        dto.setActivo(entity.getActivo());
        
        // Formatear precio
        if (entity.getPrecio() != null) {
            dto.setPrecioFormateado("S/ " + entity.getPrecio().toString());
        }
        
        return dto;
    }
    
    private List<ProductoDTO> convertirListaEntityADTO(List<Producto> entities) {
        List<ProductoDTO> dtos = new ArrayList<>();
        for (Producto entity : entities) {
            dtos.add(convertirEntityADTO(entity));
        }
        return dtos;
    }
}