/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dao.impl;
 
 
import java.sql.*; 
import sistemaventas.entity.Inventario; 
  
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import sistemaventas.dao.interfaces.IInventarioDAO;
import sistemaventas.entity.Almacen;
import sistemaventas.entity.Producto;
import sistemaventas.util.ConnectionUtil;

/**
 * Implementación del DAO para la gestión de inventario
 * Maneja todas las operaciones CRUD y consultas específicas de inventario
 *  
 * @version 1.0
 */
public class InventarioDAOImpl implements IInventarioDAO {
    
    // ============================================
    // MÉTODOS CRUD BÁSICOS
    // ============================================
    
    @Override
    public Inventario create(Inventario inventario) throws Exception {
        String sql = "INSERT INTO Inventario (idProducto, idAlmacen, cantidad, stock_minimo, stock_maximo) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, inventario.getIdProducto());
            ps.setInt(2, inventario.getIdAlmacen());
            ps.setInt(3, inventario.getCantidad());
            ps.setInt(4, inventario.getStockMinimo() != null ? inventario.getStockMinimo() : 10);
            ps.setInt(5, inventario.getStockMaximo() != null ? inventario.getStockMaximo() : 1000);
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear inventario, no se insertó ninguna fila.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    inventario.setIdInventario(generatedKeys.getInt(1));
                }
            }
            
            return inventario;
        }
    }
    
    @Override
    public Optional<Inventario> findById(Integer id) throws Exception {
        String sql = "SELECT i.idInventario, i.idProducto, i.idAlmacen, i.cantidad, " +
                     "i.stock_minimo, i.stock_maximo, i.fecha_actualizacion, i.activo, " +
                     "p.nombreproducto, p.precio, p.descripcion as producto_descripcion, " +
                     "a.descripcion as almacen_descripcion, a.direccion, a.telefono " +
                     "FROM Inventario i " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE i.idInventario = ? AND i.activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInventario(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public List<Inventario> findAll() throws Exception {
        String sql = "SELECT i.idInventario, i.idProducto, i.idAlmacen, i.cantidad, " +
                     "i.stock_minimo, i.stock_maximo, i.fecha_actualizacion, i.activo, " +
                     "p.nombreproducto, p.precio, p.descripcion as producto_descripcion, " +
                     "a.descripcion as almacen_descripcion, a.direccion, a.telefono " +
                     "FROM Inventario i " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE i.activo = TRUE " +
                     "ORDER BY p.nombreproducto, a.descripcion";
        
        List<Inventario> inventarios = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                inventarios.add(mapResultSetToInventario(rs));
            }
        }
        return inventarios;
    }
    
    @Override
    public Inventario update(Inventario inventario) throws Exception {
        String sql = "UPDATE Inventario " +
                     "SET cantidad = ?, stock_minimo = ?, stock_maximo = ?, fecha_actualizacion = CURRENT_TIMESTAMP " +
                     "WHERE idInventario = ? AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, inventario.getCantidad());
            ps.setInt(2, inventario.getStockMinimo());
            ps.setInt(3, inventario.getStockMaximo());
            ps.setInt(4, inventario.getIdInventario());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar inventario, registro no encontrado o inactivo.");
            }
            
            return inventario;
        }
    }
    
    @Override
    public boolean delete(Integer id) throws Exception {
        String sql = "UPDATE Inventario SET activo = FALSE WHERE idInventario = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    @Override
    public long count() throws Exception {
        String sql = "SELECT COUNT(*) FROM Inventario WHERE activo = TRUE";
        
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
    // MÉTODOS ESPECÍFICOS DE INVENTARIO
    // ============================================
    
    @Override
    public List<Inventario> findByAlmacen(Integer idAlmacen) throws Exception {
        String sql = "SELECT i.idInventario, i.idProducto, i.idAlmacen, i.cantidad, " +
                     "i.stock_minimo, i.stock_maximo, i.fecha_actualizacion, i.activo, " +
                     "p.nombreproducto, p.precio, p.descripcion as producto_descripcion, " +
                     "a.descripcion as almacen_descripcion, a.direccion, a.telefono " +
                     "FROM Inventario i " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE i.idAlmacen = ? AND i.activo = TRUE " +
                     "ORDER BY p.nombreproducto";
        
        List<Inventario> inventarios = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idAlmacen);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    inventarios.add(mapResultSetToInventario(rs));
                }
            }
        }
        return inventarios;
    }
    
    @Override
    public List<Inventario> findByProducto(Integer idProducto) throws Exception {
        String sql = "SELECT i.idInventario, i.idProducto, i.idAlmacen, i.cantidad, " +
                     "i.stock_minimo, i.stock_maximo, i.fecha_actualizacion, i.activo, " +
                     "p.nombreproducto, p.precio, p.descripcion as producto_descripcion, " +
                     "a.descripcion as almacen_descripcion, a.direccion, a.telefono " +
                     "FROM Inventario i " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE i.idProducto = ? AND i.activo = TRUE " +
                     "ORDER BY a.descripcion";
        
        List<Inventario> inventarios = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idProducto);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    inventarios.add(mapResultSetToInventario(rs));
                }
            }
        }
        return inventarios;
    }
    
    @Override
    public Optional<Inventario> findByProductoAndAlmacen(Integer idProducto, Integer idAlmacen) throws Exception {
        String sql = "SELECT i.idInventario, i.idProducto, i.idAlmacen, i.cantidad, " +
                     "i.stock_minimo, i.stock_maximo, i.fecha_actualizacion, i.activo, " +
                     "p.nombreproducto, p.precio, p.descripcion as producto_descripcion, " +
                     "a.descripcion as almacen_descripcion, a.direccion, a.telefono " +
                     "FROM Inventario i " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE i.idProducto = ? AND i.idAlmacen = ? AND i.activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idProducto);
            ps.setInt(2, idAlmacen);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInventario(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public List<Inventario> findStockBajo() throws Exception {
        String sql = "SELECT i.idInventario, i.idProducto, i.idAlmacen, i.cantidad, " +
                     "i.stock_minimo, i.stock_maximo, i.fecha_actualizacion, i.activo, " +
                     "p.nombreproducto, p.precio, p.descripcion as producto_descripcion, " +
                     "a.descripcion as almacen_descripcion, a.direccion, a.telefono " +
                     "FROM Inventario i " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE i.cantidad <= i.stock_minimo AND i.activo = TRUE " +
                     "ORDER BY i.cantidad ASC, p.nombreproducto";
        
        List<Inventario> inventarios = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                inventarios.add(mapResultSetToInventario(rs));
            }
        }
        return inventarios;
    }
    
    @Override
    public boolean actualizarStock(Integer idInventario, Integer cantidad) throws Exception {
        // Validación de parámetros
        if (idInventario == null || idInventario <= 0) {
            throw new IllegalArgumentException("ID de inventario inválido");
        }
        
        if (cantidad == null || cantidad < 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor o igual a cero");
        }
        
        String sql = "UPDATE Inventario " +
                     "SET cantidad = ?, fecha_actualizacion = CURRENT_TIMESTAMP " +
                     "WHERE idInventario = ? AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, cantidad);
            ps.setInt(2, idInventario);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // ============================================
    // MÉTODOS ADICIONALES PARA GESTIÓN DE STOCK
    // ============================================
    
    /**
     * Reduce el stock de un inventario específico
     * Solo permite reducir si hay suficiente stock disponible
     */
    public boolean reducirStock(Integer idInventario, Integer cantidad) throws Exception {
        if (idInventario == null || idInventario <= 0) {
            throw new IllegalArgumentException("ID de inventario inválido");
        }
        
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser mayor a cero");
        }
        
        String sql = "UPDATE Inventario " +
                     "SET cantidad = cantidad - ?, fecha_actualizacion = CURRENT_TIMESTAMP " +
                     "WHERE idInventario = ? AND cantidad >= ? AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, cantidad);
            ps.setInt(2, idInventario);
            ps.setInt(3, cantidad); // Verifica que haya suficiente stock
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Aumenta el stock de un inventario específico
     */
    public boolean aumentarStock(Integer idInventario, Integer cantidad) throws Exception {
        if (idInventario == null || idInventario <= 0) {
            throw new IllegalArgumentException("ID de inventario inválido");
        }
        
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a aumentar debe ser mayor a cero");
        }
        
        String sql = "UPDATE Inventario " +
                     "SET cantidad = cantidad + ?, fecha_actualizacion = CURRENT_TIMESTAMP " +
                     "WHERE idInventario = ? AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, cantidad);
            ps.setInt(2, idInventario);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Obtiene el stock disponible de un inventario específico
     */
    public Optional<Integer> getStockDisponible(Integer idInventario) throws Exception {
        String sql = "SELECT cantidad FROM Inventario WHERE idInventario = ? AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idInventario);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("cantidad"));
                }
            }
        }
        return Optional.empty();
    }
    
    /**
     * Busca inventarios que están por encima del stock máximo
     */
    public List<Inventario> findStockAlto() throws Exception {
        String sql = "SELECT i.idInventario, i.idProducto, i.idAlmacen, i.cantidad, " +
                     "i.stock_minimo, i.stock_maximo, i.fecha_actualizacion, i.activo, " +
                     "p.nombreproducto, p.precio, p.descripcion as producto_descripcion, " +
                     "a.descripcion as almacen_descripcion, a.direccion, a.telefono " +
                     "FROM Inventario i " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE i.cantidad >= i.stock_maximo AND i.activo = TRUE " +
                     "ORDER BY i.cantidad DESC, p.nombreproducto";
        
        List<Inventario> inventarios = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                inventarios.add(mapResultSetToInventario(rs));
            }
        }
        return inventarios;
    }
    
    /**
     * Busca inventarios con stock en rango normal (entre mínimo y máximo)
     */
    public List<Inventario> findStockNormal() throws Exception {
        String sql = "SELECT i.idInventario, i.idProducto, i.idAlmacen, i.cantidad, " +
                     "i.stock_minimo, i.stock_maximo, i.fecha_actualizacion, i.activo, " +
                     "p.nombreproducto, p.precio, p.descripcion as producto_descripcion, " +
                     "a.descripcion as almacen_descripcion, a.direccion, a.telefono " +
                     "FROM Inventario i " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE i.cantidad > i.stock_minimo AND i.cantidad < i.stock_maximo AND i.activo = TRUE " +
                     "ORDER BY p.nombreproducto, a.descripcion";
        
        List<Inventario> inventarios = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                inventarios.add(mapResultSetToInventario(rs));
            }
        }
        return inventarios;
    }
    
    /**
     * Obtiene estadísticas generales del inventario
     */
    public InventarioStats getInventarioStats() throws Exception {
        String sql = "SELECT " +
                     "COUNT(*) as total_items, " +
                     "SUM(cantidad) as total_stock, " +
                     "SUM(cantidad * p.precio) as valor_total, " +
                     "AVG(cantidad) as promedio_stock, " +
                     "SUM(CASE WHEN cantidad <= stock_minimo THEN 1 ELSE 0 END) as items_stock_bajo, " +
                     "SUM(CASE WHEN cantidad >= stock_maximo THEN 1 ELSE 0 END) as items_stock_alto " +
                     "FROM Inventario i " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "WHERE i.activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return new InventarioStats(
                    rs.getInt("total_items"),
                    rs.getInt("total_stock"),
                    rs.getBigDecimal("valor_total"),
                    rs.getDouble("promedio_stock"),
                    rs.getInt("items_stock_bajo"),
                    rs.getInt("items_stock_alto")
                );
            }
        }
        return new InventarioStats(0, 0, java.math.BigDecimal.ZERO, 0.0, 0, 0);
    } 
    
    
    // ============================================
    // MÉTODOS PRIVADOS DE MAPEO
    // ============================================
    
    /**
     * Convierte un ResultSet en un objeto Inventario con sus relaciones
     */
    private Inventario mapResultSetToInventario(ResultSet rs) throws SQLException {
        Inventario inventario = new Inventario();
        
        // Mapear campos del inventario
        inventario.setIdInventario(rs.getInt("idInventario"));
        inventario.setIdProducto(rs.getInt("idProducto"));
        inventario.setIdAlmacen(rs.getInt("idAlmacen"));
        inventario.setCantidad(rs.getInt("cantidad"));
        inventario.setStockMinimo(rs.getInt("stock_minimo"));
        inventario.setStockMaximo(rs.getInt("stock_maximo"));
        inventario.setActivo(rs.getBoolean("activo"));
        
        // Mapear fecha de actualización
        Timestamp timestamp = rs.getTimestamp("fecha_actualizacion");
        if (timestamp != null) {
            inventario.setFechaActualizacion(timestamp.toLocalDateTime());
        }
        
        // Mapear Producto relacionado
        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("idProducto"));
        producto.setNombreproducto(rs.getString("nombreproducto"));
        producto.setPrecio(rs.getBigDecimal("precio"));
        producto.setDescripcion(rs.getString("producto_descripcion"));
        inventario.setProducto(producto);
        
        // Mapear Almacén relacionado
        Almacen almacen = new Almacen();
        almacen.setIdAlmacen(rs.getInt("idAlmacen"));
        almacen.setDescripcion(rs.getString("almacen_descripcion"));
        almacen.setDireccion(rs.getString("direccion"));
        almacen.setTelefono(rs.getString("telefono"));
        inventario.setAlmacen(almacen);
        
        return inventario;
    }
    
    // ============================================
    // CLASE INTERNA PARA ESTADÍSTICAS
    // ============================================
    
    /**
     * Clase para encapsular estadísticas del inventario
     */
    public static class InventarioStats {
        private final int totalItems;
        private final int totalStock;
        private final java.math.BigDecimal valorTotal;
        private final double promedioStock;
        private final int itemsStockBajo;
        private final int itemsStockAlto;
        
        public InventarioStats(int totalItems, int totalStock, java.math.BigDecimal valorTotal, 
                              double promedioStock, int itemsStockBajo, int itemsStockAlto) {
            this.totalItems = totalItems;
            this.totalStock = totalStock;
            this.valorTotal = valorTotal;
            this.promedioStock = promedioStock;
            this.itemsStockBajo = itemsStockBajo;
            this.itemsStockAlto = itemsStockAlto;
        }
        
        // Getters
        public int getTotalItems() { return totalItems; }
        public int getTotalStock() { return totalStock; }
        public java.math.BigDecimal getValorTotal() { return valorTotal; }
        public double getPromedioStock() { return promedioStock; }
        public int getItemsStockBajo() { return itemsStockBajo; }
        public int getItemsStockAlto() { return itemsStockAlto; }
        public int getItemsStockNormal() { return totalItems - itemsStockBajo - itemsStockAlto; }
        
        @Override
        public String toString() {
            return String.format(
                "InventarioStats{totalItems=%d, totalStock=%d, valorTotal=%s, " +
                "promedioStock=%.2f, stockBajo=%d, stockAlto=%d, stockNormal=%d}",
                totalItems, totalStock, valorTotal, promedioStock, 
                itemsStockBajo, itemsStockAlto, getItemsStockNormal()
            );
        }
    }
}