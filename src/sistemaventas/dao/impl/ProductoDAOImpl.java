/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dao.impl; 

import sistemaventas.dao.interfaces.IProductoDAO;
import sistemaventas.entity.Producto;
import sistemaventas.util.ConnectionUtil;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.math.BigDecimal;

/**
 * Implementación del DAO para la gestión de productos
 * Maneja todas las operaciones CRUD y consultas específicas de productos
 *  
 * @version 1.0
 */
public class ProductoDAOImpl implements IProductoDAO {
    
    // ============================================
    // MÉTODOS CRUD BÁSICOS
    // ============================================
    
    @Override
    public Producto create(Producto producto) throws Exception {
        String sql = "INSERT INTO Productos (nombreproducto, precio, descripcion) VALUES (?, ?, ?)";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, producto.getNombreproducto());
            ps.setBigDecimal(2, producto.getPrecio());
            ps.setString(3, producto.getDescripcion());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear producto, no se insertó ninguna fila.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    producto.setIdProducto(generatedKeys.getInt(1));
                }
            }
            
            return producto;
        }
    }
    
    @Override
    public Optional<Producto> findById(Integer id) throws Exception {
        String sql = "SELECT * FROM Productos WHERE idProducto = ? AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProducto(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public List<Producto> findAll() throws Exception {
        String sql = "SELECT * FROM Productos WHERE activo = TRUE ORDER BY nombreproducto";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        }
        return productos;
    }
    
    @Override
    public Producto update(Producto producto) throws Exception {
        String sql = "UPDATE Productos SET nombreproducto = ?, precio = ?, descripcion = ? WHERE idProducto = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, producto.getNombreproducto());
            ps.setBigDecimal(2, producto.getPrecio());
            ps.setString(3, producto.getDescripcion());
            ps.setInt(4, producto.getIdProducto());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar producto, producto no encontrado.");
            }
            
            return producto;
        }
    }
    
    @Override
    public boolean delete(Integer id) throws Exception {
        String sql = "UPDATE Productos SET activo = FALSE WHERE idProducto = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    @Override
    public long count() throws Exception {
        String sql = "SELECT COUNT(*) FROM Productos WHERE activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }
    
    // ============================================
    // MÉTODOS ESPECÍFICOS DE PRODUCTO
    // ============================================
    
    @Override
    public List<Producto> findByNombre(String nombre) throws Exception {
        String sql = "SELECT * FROM Productos WHERE nombreproducto LIKE ? AND activo = TRUE ORDER BY nombreproducto";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, "%" + nombre + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        }
        return productos;
    }
    
    @Override
    public List<Producto> findByPrecioRange(BigDecimal precioMin, BigDecimal precioMax) throws Exception {
        String sql = "SELECT * FROM Productos WHERE precio BETWEEN ? AND ? AND activo = TRUE ORDER BY precio";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setBigDecimal(1, precioMin);
            ps.setBigDecimal(2, precioMax);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        }
        return productos;
    }
    
    @Override
    public List<Producto> findActivos() throws Exception {
        return findAll();
    }
    
    @Override
    public List<Producto> findMasCarosQue(BigDecimal precio) throws Exception {
        String sql = "SELECT * FROM Productos WHERE precio > ? AND activo = TRUE ORDER BY precio DESC";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setBigDecimal(1, precio);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        }
        return productos;
    }
    
    @Override
    public List<Producto> findMasBaratosQue(BigDecimal precio) throws Exception {
        String sql = "SELECT * FROM Productos WHERE precio < ? AND activo = TRUE ORDER BY precio ASC";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setBigDecimal(1, precio);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        }
        return productos;
    }
    
    @Override
    public List<Producto> findMasVendidos(int limite) throws Exception {
        String sql = "SELECT p.*, SUM(dv.cantidad) as total_vendido " +
                     "FROM Productos p " +
                     "JOIN Inventario i ON p.idProducto = i.idProducto " +
                     "JOIN DetalleVenta dv ON i.idInventario = dv.idInventario " +
                     "JOIN Ventas v ON dv.idVentas = v.idVentas " +
                     "WHERE p.activo = TRUE AND v.estado = 'COMPLETADA' " +
                     "GROUP BY p.idProducto, p.nombreproducto, p.precio, p.descripcion, p.fecha_registro, p.activo " +
                     "ORDER BY total_vendido DESC " +
                     "LIMIT ?";
        
        List<Producto> productos = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, limite);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        }
        return productos;
    }
    
    @Override
    public long countActivos() throws Exception {
        return count();
    }
    
    // ============================================
    // MÉTODOS ADICIONALES ESPECÍFICOS
    // ============================================
    
    /**
     * Busca productos por descripción (búsqueda parcial)
     * @param descripcion Descripción o parte de la descripción del producto
     * @return Lista de productos que coinciden
     * @throws Exception Si ocurre un error en la consulta
     */
    public List<Producto> findByDescripcion(String descripcion) throws Exception {
        String sql = "SELECT * FROM Productos WHERE descripcion LIKE ? AND activo = TRUE ORDER BY nombreproducto";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, "%" + descripcion + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        }
        return productos;
    }
    
    /**
     * Busca productos que no tienen inventario en ningún almacén
     * @return Lista de productos sin inventario
     * @throws Exception Si ocurre un error en la consulta
     */
    public List<Producto> findSinInventario() throws Exception {
        String sql = "SELECT p.* FROM Productos p " +
                     "LEFT JOIN Inventario i ON p.idProducto = i.idProducto AND i.activo = TRUE " +
                     "WHERE p.activo = TRUE AND i.idProducto IS NULL " +
                     "ORDER BY p.nombreproducto";
        
        List<Producto> productos = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        }
        return productos;
    }
    
    /**
     * Busca productos con stock disponible
     * @return Lista de productos con stock mayor a 0
     * @throws Exception Si ocurre un error en la consulta
     */
    public List<Producto> findConStock() throws Exception {
        String sql = "SELECT DISTINCT p.* FROM Productos p " +
                     "JOIN Inventario i ON p.idProducto = i.idProducto " +
                     "WHERE p.activo = TRUE AND i.activo = TRUE AND i.cantidad > 0 " +
                     "ORDER BY p.nombreproducto";
        
        List<Producto> productos = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        }
        return productos;
    }
    
    /**
     * Busca productos ordenados por precio (ascendente o descendente)
     * @param ascending true para orden ascendente, false para descendente
     * @return Lista de productos ordenados por precio
     * @throws Exception Si ocurre un error en la consulta
     */
    public List<Producto> findOrderByPrecio(boolean ascending) throws Exception {
        String order = ascending ? "ASC" : "DESC";
        String sql = "SELECT * FROM Productos WHERE activo = TRUE ORDER BY precio " + order;
        
        List<Producto> productos = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        }
        return productos;
    }
    
    /**
     * Busca productos por un rango de fechas de registro
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha fin del rango
     * @return Lista de productos registrados en el rango
     * @throws Exception Si ocurre un error en la consulta
     */
    public List<Producto> findByFechaRegistro(java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin) throws Exception {
        String sql = "SELECT * FROM Productos WHERE DATE(fecha_registro) BETWEEN ? AND ? AND activo = TRUE ORDER BY fecha_registro DESC";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        }
        return productos;
    }
    
    /**
     * Obtiene el precio promedio de todos los productos activos
     * @return Precio promedio
     * @throws Exception Si ocurre un error en la consulta
     */
    public BigDecimal getPrecioPromedio() throws Exception {
        String sql = "SELECT AVG(precio) as precio_promedio FROM Productos WHERE activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                BigDecimal promedio = rs.getBigDecimal("precio_promedio");
                return promedio != null ? promedio : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Obtiene el producto más caro
     * @return Optional con el producto más caro
     * @throws Exception Si ocurre un error en la consulta
     */
    public Optional<Producto> findMasCaro() throws Exception {
        String sql = "SELECT * FROM Productos WHERE activo = TRUE ORDER BY precio DESC LIMIT 1";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return Optional.of(mapResultSetToProducto(rs));
            }
        }
        return Optional.empty();
    }
    
    /**
     * Obtiene el producto más barato
     * @return Optional con el producto más barato
     * @throws Exception Si ocurre un error en la consulta
     */
    public Optional<Producto> findMasBarato() throws Exception {
        String sql = "SELECT * FROM Productos WHERE activo = TRUE ORDER BY precio ASC LIMIT 1";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return Optional.of(mapResultSetToProducto(rs));
            }
        }
        return Optional.empty();
    }
    
    /**
     * Busca productos por nombre exacto (case-insensitive)
     * @param nombre Nombre exacto del producto
     * @return Optional con el producto si existe
     * @throws Exception Si ocurre un error en la consulta
     */
    public Optional<Producto> findByNombreExacto(String nombre) throws Exception {
        String sql = "SELECT * FROM Productos WHERE LOWER(nombreproducto) = LOWER(?) AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProducto(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    /**
     * Cuenta productos por rango de precio
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     * @return Número de productos en el rango
     * @throws Exception Si ocurre un error en la consulta
     */
    public long countByPrecioRange(BigDecimal precioMin, BigDecimal precioMax) throws Exception {
        String sql = "SELECT COUNT(*) FROM Productos WHERE precio BETWEEN ? AND ? AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setBigDecimal(1, precioMin);
            ps.setBigDecimal(2, precioMax);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Actualiza solo el precio de un producto
     * @param idProducto ID del producto
     * @param nuevoPrecio Nuevo precio del producto
     * @return true si se actualizó correctamente
     * @throws Exception Si ocurre un error en la actualización
     */
    public boolean actualizarPrecio(Integer idProducto, BigDecimal nuevoPrecio) throws Exception {
        String sql = "UPDATE Productos SET precio = ? WHERE idProducto = ? AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setBigDecimal(1, nuevoPrecio);
            ps.setInt(2, idProducto);
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Verifica si existe un producto con el nombre especificado (excluyendo un ID)
     * @param nombre Nombre del producto
     * @param excludeId ID a excluir de la búsqueda (para updates)
     * @return true si existe otro producto con ese nombre
     * @throws Exception Si ocurre un error en la consulta
     */
    public boolean existeNombreProducto(String nombre, Integer excludeId) throws Exception {
        String sql = "SELECT COUNT(*) FROM Productos WHERE LOWER(nombreproducto) = LOWER(?) AND idProducto != ? AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            ps.setInt(2, excludeId != null ? excludeId : -1);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Obtiene estadísticas de productos por rango de precios
     * @return ProductoStats con estadísticas generales
     * @throws Exception Si ocurre un error en la consulta
     */
    public ProductoStats getEstadisticasProductos() throws Exception {
        String sql = "SELECT " +
                     "COUNT(*) as total_productos, " +
                     "AVG(precio) as precio_promedio, " +
                     "MIN(precio) as precio_minimo, " +
                     "MAX(precio) as precio_maximo, " +
                     "SUM(CASE WHEN precio < 100 THEN 1 ELSE 0 END) as productos_baratos, " +
                     "SUM(CASE WHEN precio >= 100 AND precio <= 1000 THEN 1 ELSE 0 END) as productos_medios, " +
                     "SUM(CASE WHEN precio > 1000 THEN 1 ELSE 0 END) as productos_caros " +
                     "FROM Productos WHERE activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return new ProductoStats(
                    rs.getInt("total_productos"),
                    rs.getBigDecimal("precio_promedio"),
                    rs.getBigDecimal("precio_minimo"),
                    rs.getBigDecimal("precio_maximo"),
                    rs.getInt("productos_baratos"),
                    rs.getInt("productos_medios"),
                    rs.getInt("productos_caros")
                );
            }
        }
        return new ProductoStats(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0, 0);
    }
    
    // ============================================
    // MÉTODO PRIVADO DE MAPEO
    // ============================================
    
    /**
     * Convierte un ResultSet en un objeto Producto
     */
    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("idProducto"));
        producto.setNombreproducto(rs.getString("nombreproducto"));
        producto.setPrecio(rs.getBigDecimal("precio"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setActivo(rs.getBoolean("activo"));
        
        Timestamp timestamp = rs.getTimestamp("fecha_registro");
        if (timestamp != null) {
            producto.setFechaRegistro(timestamp.toLocalDateTime());
        }
        
        return producto;
    }
    
    // ============================================
    // CLASE INTERNA PARA ESTADÍSTICAS
    // ============================================
    
    /**
     * Clase para encapsular estadísticas de productos
     */
    public static class ProductoStats {
        private final int totalProductos;
        private final BigDecimal precioPromedio;
        private final BigDecimal precioMinimo;
        private final BigDecimal precioMaximo;
        private final int productosBaratos;
        private final int productosMedios;
        private final int productosCaros;
        
        public ProductoStats(int totalProductos, BigDecimal precioPromedio, BigDecimal precioMinimo, 
                           BigDecimal precioMaximo, int productosBaratos, int productosMedios, int productosCaros) {
            this.totalProductos = totalProductos;
            this.precioPromedio = precioPromedio;
            this.precioMinimo = precioMinimo;
            this.precioMaximo = precioMaximo;
            this.productosBaratos = productosBaratos;
            this.productosMedios = productosMedios;
            this.productosCaros = productosCaros;
        }
        
        // Getters
        public int getTotalProductos() { return totalProductos; }
        public BigDecimal getPrecioPromedio() { return precioPromedio; }
        public BigDecimal getPrecioMinimo() { return precioMinimo; }
        public BigDecimal getPrecioMaximo() { return precioMaximo; }
        public int getProductosBaratos() { return productosBaratos; }
        public int getProductosMedios() { return productosMedios; }
        public int getProductosCaros() { return productosCaros; }
        
        @Override
        public String toString() {
            return String.format(
                "ProductoStats{total=%d, promedio=%s, min=%s, max=%s, baratos=%d, medios=%d, caros=%d}",
                totalProductos, precioPromedio, precioMinimo, precioMaximo, 
                productosBaratos, productosMedios, productosCaros
            );
        }
    }
}