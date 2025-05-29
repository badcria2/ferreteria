/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dao.impl;
 
import sistemaventas.dao.interfaces.IDetalleVentaDAO;
import sistemaventas.entity.DetalleVenta;
import sistemaventas.entity.Inventario;
import sistemaventas.entity.Producto;
import sistemaventas.entity.Almacen;
import sistemaventas.util.ConnectionUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del DAO para la gestión de detalles de venta
 * Maneja todas las operaciones CRUD y consultas específicas de detalles de venta
 *  
 * @version 1.0
 */
public class DetalleVentaDAOImpl implements IDetalleVentaDAO {
    
    // ============================================
    // MÉTODOS CRUD BÁSICOS
    // ============================================
    
    @Override
    public DetalleVenta create(DetalleVenta detalleVenta) throws Exception {
        String sql = "INSERT INTO DetalleVenta (idVentas, idInventario, cantidad, precio_unitario, preciototal) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, detalleVenta.getIdVentas());
            ps.setInt(2, detalleVenta.getIdInventario());
            ps.setInt(3, detalleVenta.getCantidad());
            ps.setBigDecimal(4, detalleVenta.getPrecioUnitario());
            ps.setBigDecimal(5, detalleVenta.getPreciototal());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear detalle de venta, no se insertó ninguna fila.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    detalleVenta.setIdDetalleVenta(generatedKeys.getInt(1));
                }
            }
            
            return detalleVenta;
        }
    }
    
    @Override
    public Optional<DetalleVenta> findById(Integer id) throws Exception {
        String sql = "SELECT dv.idDetalleVenta, dv.idVentas, dv.idInventario, dv.cantidad, " +
                     "dv.precio_unitario, dv.preciototal, dv.fecha_registro, " +
                     "p.idProducto, p.nombreproducto, p.precio as precio_actual, p.descripcion as producto_descripcion, " +
                     "a.idAlmacen, a.descripcion as almacen_descripcion, a.direccion, a.telefono, " +
                     "i.cantidad as stock_disponible, i.stock_minimo, i.stock_maximo " +
                     "FROM DetalleVenta dv " +
                     "JOIN Inventario i ON dv.idInventario = i.idInventario " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE dv.idDetalleVenta = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDetalleVenta(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public List<DetalleVenta> findAll() throws Exception {
        String sql = "SELECT dv.idDetalleVenta, dv.idVentas, dv.idInventario, dv.cantidad, " +
                     "dv.precio_unitario, dv.preciototal, dv.fecha_registro, " +
                     "p.idProducto, p.nombreproducto, p.precio as precio_actual, p.descripcion as producto_descripcion, " +
                     "a.idAlmacen, a.descripcion as almacen_descripcion, a.direccion, a.telefono, " +
                     "i.cantidad as stock_disponible, i.stock_minimo, i.stock_maximo " +
                     "FROM DetalleVenta dv " +
                     "JOIN Inventario i ON dv.idInventario = i.idInventario " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "ORDER BY dv.fecha_registro DESC";
        
        List<DetalleVenta> detalles = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                detalles.add(mapResultSetToDetalleVenta(rs));
            }
        }
        return detalles;
    }
    
    @Override
    public DetalleVenta update(DetalleVenta detalleVenta) throws Exception {
        String sql = "UPDATE DetalleVenta SET cantidad = ?, precio_unitario = ?, preciototal = ? WHERE idDetalleVenta = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, detalleVenta.getCantidad());
            ps.setBigDecimal(2, detalleVenta.getPrecioUnitario());
            ps.setBigDecimal(3, detalleVenta.getPreciototal());
            ps.setInt(4, detalleVenta.getIdDetalleVenta());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar detalle de venta, registro no encontrado.");
            }
            
            return detalleVenta;
        }
    }
    
    @Override
    public boolean delete(Integer id) throws Exception {
        String sql = "DELETE FROM DetalleVenta WHERE idDetalleVenta = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    @Override
    public long count() throws Exception {
        String sql = "SELECT COUNT(*) FROM DetalleVenta";
        
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
    // MÉTODOS ESPECÍFICOS DE DETALLE VENTA
    // ============================================
    
    @Override
    public List<DetalleVenta> findByVenta(Integer idVenta) throws Exception {
        String sql = "SELECT dv.idDetalleVenta, dv.idVentas, dv.idInventario, dv.cantidad, " +
                     "dv.precio_unitario, dv.preciototal, dv.fecha_registro, " +
                     "p.idProducto, p.nombreproducto, p.precio as precio_actual, p.descripcion as producto_descripcion, " +
                     "a.idAlmacen, a.descripcion as almacen_descripcion, a.direccion, a.telefono, " +
                     "i.cantidad as stock_disponible, i.stock_minimo, i.stock_maximo " +
                     "FROM DetalleVenta dv " +
                     "JOIN Inventario i ON dv.idInventario = i.idInventario " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE dv.idVentas = ? " +
                     "ORDER BY dv.idDetalleVenta";
        
        List<DetalleVenta> detalles = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idVenta);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapResultSetToDetalleVenta(rs));
                }
            }
        }
        return detalles;
    }
    
    @Override
    public List<DetalleVenta> findByInventario(Integer idInventario) throws Exception {
        String sql = "SELECT dv.idDetalleVenta, dv.idVentas, dv.idInventario, dv.cantidad, " +
                     "dv.precio_unitario, dv.preciototal, dv.fecha_registro, " +
                     "p.idProducto, p.nombreproducto, p.precio as precio_actual, p.descripcion as producto_descripcion, " +
                     "a.idAlmacen, a.descripcion as almacen_descripcion, a.direccion, a.telefono, " +
                     "i.cantidad as stock_disponible, i.stock_minimo, i.stock_maximo " +
                     "FROM DetalleVenta dv " +
                     "JOIN Inventario i ON dv.idInventario = i.idInventario " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE dv.idInventario = ? " +
                     "ORDER BY dv.fecha_registro DESC";
        
        List<DetalleVenta> detalles = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idInventario);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapResultSetToDetalleVenta(rs));
                }
            }
        }
        return detalles;
    }
    
    @Override
    public List<DetalleVenta> findByProducto(Integer idProducto) throws Exception {
        String sql = "SELECT dv.idDetalleVenta, dv.idVentas, dv.idInventario, dv.cantidad, " +
                     "dv.precio_unitario, dv.preciototal, dv.fecha_registro, " +
                     "p.idProducto, p.nombreproducto, p.precio as precio_actual, p.descripcion as producto_descripcion, " +
                     "a.idAlmacen, a.descripcion as almacen_descripcion, a.direccion, a.telefono, " +
                     "i.cantidad as stock_disponible, i.stock_minimo, i.stock_maximo " +
                     "FROM DetalleVenta dv " +
                     "JOIN Inventario i ON dv.idInventario = i.idInventario " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE p.idProducto = ? " +
                     "ORDER BY dv.fecha_registro DESC";
        
        List<DetalleVenta> detalles = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idProducto);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapResultSetToDetalleVenta(rs));
                }
            }
        }
        return detalles;
    }
    
    @Override
    public Integer calcularCantidadVendida(Integer idProducto, LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT SUM(dv.cantidad) as total_vendido " +
                     "FROM DetalleVenta dv " +
                     "JOIN Inventario i ON dv.idInventario = i.idInventario " +
                     "JOIN Ventas v ON dv.idVentas = v.idVentas " +
                     "WHERE i.idProducto = ? AND v.fecha BETWEEN ? AND ? AND v.estado = 'COMPLETADA'";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idProducto);
            ps.setDate(2, Date.valueOf(fechaInicio));
            ps.setDate(3, Date.valueOf(fechaFin));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer total = rs.getInt("total_vendido");
                    return rs.wasNull() ? 0 : total;
                }
            }
        }
        return 0;
    }
    
    @Override
    public List<DetalleVenta> findProductosMasVendidos(int limite) throws Exception {
        String sql = "SELECT dv.idDetalleVenta, dv.idVentas, dv.idInventario, " +
                     "SUM(dv.cantidad) as cantidad_vendida, " +
                     "AVG(dv.precio_unitario) as precio_promedio, " +
                     "SUM(dv.preciototal) as total_ingresos, " +
                     "p.idProducto, p.nombreproducto, p.precio as precio_actual, p.descripcion as producto_descripcion, " +
                     "a.idAlmacen, a.descripcion as almacen_descripcion, a.direccion, a.telefono, " +
                     "i.cantidad as stock_disponible, i.stock_minimo, i.stock_maximo, " +
                     "MIN(dv.fecha_registro) as primera_venta, MAX(dv.fecha_registro) as ultima_venta " +
                     "FROM DetalleVenta dv " +
                     "JOIN Inventario i ON dv.idInventario = i.idInventario " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "JOIN Ventas v ON dv.idVentas = v.idVentas " +
                     "WHERE v.estado = 'COMPLETADA' " +
                     "GROUP BY p.idProducto, p.nombreproducto, p.precio, p.descripcion, " +
                     "a.idAlmacen, a.descripcion, a.direccion, a.telefono, " +
                     "i.cantidad, i.stock_minimo, i.stock_maximo, dv.idInventario " +
                     "ORDER BY cantidad_vendida DESC " +
                     "LIMIT ?";
        
        List<DetalleVenta> detalles = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, limite);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta detalle = mapResultSetToDetalleVenta(rs);
                    
                    // Agregar información adicional de estadísticas
                    detalle.setCantidad(rs.getInt("cantidad_vendida"));
                    detalle.setPrecioUnitario(rs.getBigDecimal("precio_promedio"));
                    detalle.setPreciototal(rs.getBigDecimal("total_ingresos"));
                    
                    detalles.add(detalle);
                }
            }
        }
        return detalles;
    }
    
    @Override
    public boolean deleteByVenta(Integer idVenta) throws Exception {
        String sql = "DELETE FROM DetalleVenta WHERE idVentas = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idVenta);
            return ps.executeUpdate() > 0;
        }
    }
    
    @Override
    public long countItemsByVenta(Integer idVenta) throws Exception {
        String sql = "SELECT COUNT(*) FROM DetalleVenta WHERE idVentas = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idVenta);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }
    
    @Override
    public Integer calcularCantidadTotalByVenta(Integer idVenta) throws Exception {
        String sql = "SELECT SUM(cantidad) as total_cantidad FROM DetalleVenta WHERE idVentas = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idVenta);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer total = rs.getInt("total_cantidad");
                    return rs.wasNull() ? 0 : total;
                }
            }
        }
        return 0;
    }
    
    // ============================================
    // MÉTODOS ADICIONALES ESPECÍFICOS
    // ============================================
    
    /**
     * Busca detalles de venta por cliente específico
     * @param idCliente ID del cliente
     * @return Lista de detalles de venta del cliente
     * @throws Exception Si ocurre un error en la consulta
     */
    public List<DetalleVenta> findByCliente(Integer idCliente) throws Exception {
        String sql = "SELECT dv.idDetalleVenta, dv.idVentas, dv.idInventario, dv.cantidad, " +
                     "dv.precio_unitario, dv.preciototal, dv.fecha_registro, " +
                     "p.idProducto, p.nombreproducto, p.precio as precio_actual, p.descripcion as producto_descripcion, " +
                     "a.idAlmacen, a.descripcion as almacen_descripcion, a.direccion, a.telefono, " +
                     "i.cantidad as stock_disponible, i.stock_minimo, i.stock_maximo " +
                     "FROM DetalleVenta dv " +
                     "JOIN Inventario i ON dv.idInventario = i.idInventario " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "JOIN Ventas v ON dv.idVentas = v.idVentas " +
                     "WHERE v.idCliente = ? " +
                     "ORDER BY dv.fecha_registro DESC";
        
        List<DetalleVenta> detalles = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idCliente);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapResultSetToDetalleVenta(rs));
                }
            }
        }
        return detalles;
    }
    
    /**
     * Busca detalles de venta por almacén específico
     * @param idAlmacen ID del almacén
     * @return Lista de detalles de venta del almacén
     * @throws Exception Si ocurre un error en la consulta
     */
    public List<DetalleVenta> findByAlmacen(Integer idAlmacen) throws Exception {
        String sql = "SELECT dv.idDetalleVenta, dv.idVentas, dv.idInventario, dv.cantidad, " +
                     "dv.precio_unitario, dv.preciototal, dv.fecha_registro, " +
                     "p.idProducto, p.nombreproducto, p.precio as precio_actual, p.descripcion as producto_descripcion, " +
                     "a.idAlmacen, a.descripcion as almacen_descripcion, a.direccion, a.telefono, " +
                     "i.cantidad as stock_disponible, i.stock_minimo, i.stock_maximo " +
                     "FROM DetalleVenta dv " +
                     "JOIN Inventario i ON dv.idInventario = i.idInventario " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "WHERE a.idAlmacen = ? " +
                     "ORDER BY dv.fecha_registro DESC";
        
        List<DetalleVenta> detalles = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idAlmacen);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapResultSetToDetalleVenta(rs));
                }
            }
        }
        return detalles;
    }
    
    /**
     * Calcula el total de ingresos por producto en un período
     * @param idProducto ID del producto
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha fin del período
     * @return Total de ingresos del producto
     * @throws Exception Si ocurre un error en la consulta
     */
    public java.math.BigDecimal calcularIngresosPorProducto(Integer idProducto, LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT SUM(dv.preciototal) as total_ingresos " +
                     "FROM DetalleVenta dv " +
                     "JOIN Inventario i ON dv.idInventario = i.idInventario " +
                     "JOIN Ventas v ON dv.idVentas = v.idVentas " +
                     "WHERE i.idProducto = ? AND v.fecha BETWEEN ? AND ? AND v.estado = 'COMPLETADA'";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idProducto);
            ps.setDate(2, Date.valueOf(fechaInicio));
            ps.setDate(3, Date.valueOf(fechaFin));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.math.BigDecimal total = rs.getBigDecimal("total_ingresos");
                    return total != null ? total : java.math.BigDecimal.ZERO;
                }
            }
        }
        return java.math.BigDecimal.ZERO;
    }
    
    /**
     * Obtiene estadísticas de ventas por producto
     * @param idProducto ID del producto
     * @return Estadísticas del producto
     * @throws Exception Si ocurre un error en la consulta
     */
    public ProductoVentaStats getEstadisticasProducto(Integer idProducto) throws Exception {
        String sql = "SELECT " +
                     "COUNT(DISTINCT dv.idVentas) as total_ventas, " +
                     "SUM(dv.cantidad) as cantidad_total_vendida, " +
                     "AVG(dv.cantidad) as cantidad_promedio_por_venta, " +
                     "SUM(dv.preciototal) as ingresos_totales, " +
                     "AVG(dv.preciototal) as ingreso_promedio_por_venta, " +
                     "MIN(dv.precio_unitario) as precio_minimo_vendido, " +
                     "MAX(dv.precio_unitario) as precio_maximo_vendido, " +
                     "AVG(dv.precio_unitario) as precio_promedio_vendido, " +
                     "MIN(v.fecha) as primera_venta, " +
                     "MAX(v.fecha) as ultima_venta " +
                     "FROM DetalleVenta dv " +
                     "JOIN Inventario i ON dv.idInventario = i.idInventario " +
                     "JOIN Ventas v ON dv.idVentas = v.idVentas " +
                     "WHERE i.idProducto = ? AND v.estado = 'COMPLETADA'";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idProducto);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ProductoVentaStats(
                        rs.getInt("total_ventas"),
                        rs.getInt("cantidad_total_vendida"),
                        rs.getDouble("cantidad_promedio_por_venta"),
                        rs.getBigDecimal("ingresos_totales"),
                        rs.getBigDecimal("ingreso_promedio_por_venta"),
                        rs.getBigDecimal("precio_minimo_vendido"),
                        rs.getBigDecimal("precio_maximo_vendido"),
                        rs.getBigDecimal("precio_promedio_vendido"),
                        rs.getDate("primera_venta") != null ? rs.getDate("primera_venta").toLocalDate() : null,
                        rs.getDate("ultima_venta") != null ? rs.getDate("ultima_venta").toLocalDate() : null
                    );
                }
            }
        }
        return new ProductoVentaStats(0, 0, 0.0, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO, 
                                     java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO, null, null);
    }
    
    /**
     * Busca detalles de venta en un rango de fechas
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha fin del rango
     * @return Lista de detalles en el rango de fechas
     * @throws Exception Si ocurre un error en la consulta
     */
    public List<DetalleVenta> findByFechaRange(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT dv.idDetalleVenta, dv.idVentas, dv.idInventario, dv.cantidad, " +
                     "dv.precio_unitario, dv.preciototal, dv.fecha_registro, " +
                     "p.idProducto, p.nombreproducto, p.precio as precio_actual, p.descripcion as producto_descripcion, " +
                     "a.idAlmacen, a.descripcion as almacen_descripcion, a.direccion, a.telefono, " +
                     "i.cantidad as stock_disponible, i.stock_minimo, i.stock_maximo " +
                     "FROM DetalleVenta dv " +
                     "JOIN Inventario i ON dv.idInventario = i.idInventario " +
                     "JOIN Productos p ON i.idProducto = p.idProducto " +
                     "JOIN Almacen a ON i.idAlmacen = a.idAlmacen " +
                     "JOIN Ventas v ON dv.idVentas = v.idVentas " +
                     "WHERE v.fecha BETWEEN ? AND ? " +
                     "ORDER BY v.fecha DESC, dv.fecha_registro DESC";
        
        List<DetalleVenta> detalles = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapResultSetToDetalleVenta(rs));
                }
            }
        }
        return detalles;
    }
    
    // ============================================
    // MÉTODO PRIVADO DE MAPEO
    // ============================================
    
    /**
     * Convierte un ResultSet en un objeto DetalleVenta con sus relaciones
     */
    private DetalleVenta mapResultSetToDetalleVenta(ResultSet rs) throws SQLException {
        DetalleVenta detalle = new DetalleVenta();
        
        // Mapear campos del detalle de venta
        detalle.setIdDetalleVenta(rs.getInt("idDetalleVenta"));
        detalle.setIdVentas(rs.getInt("idVentas"));
        detalle.setIdInventario(rs.getInt("idInventario"));
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        detalle.setPreciototal(rs.getBigDecimal("preciototal"));
        
        // Mapear fecha de registro
        Timestamp timestamp = rs.getTimestamp("fecha_registro");
        if (timestamp != null) {
            detalle.setFechaRegistro(timestamp.toLocalDateTime());
        }
        
        // Crear y mapear Inventario relacionado
        Inventario inventario = new Inventario();
        inventario.setIdInventario(rs.getInt("idInventario"));
        inventario.setCantidad(rs.getInt("stock_disponible"));
        inventario.setStockMinimo(rs.getInt("stock_minimo"));
        inventario.setStockMaximo(rs.getInt("stock_maximo"));
        
        // Crear y mapear Producto relacionado
        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("idProducto"));
        producto.setNombreproducto(rs.getString("nombreproducto"));
        producto.setPrecio(rs.getBigDecimal("precio_actual"));
        producto.setDescripcion(rs.getString("producto_descripcion"));
        
        // Crear y mapear Almacén relacionado
        Almacen almacen = new Almacen();
        almacen.setIdAlmacen(rs.getInt("idAlmacen"));
        almacen.setDescripcion(rs.getString("almacen_descripcion"));
        almacen.setDireccion(rs.getString("direccion"));
        almacen.setTelefono(rs.getString("telefono"));
        
        // Establecer relaciones
        inventario.setProducto(producto);
        inventario.setAlmacen(almacen);
        detalle.setInventario(inventario);
        
        return detalle;
    }
    
    // ============================================
    // CLASE INTERNA PARA ESTADÍSTICAS DE PRODUCTO
    // ============================================
    
    /**
     * Clase para encapsular estadísticas de ventas por producto
     */
    public static class ProductoVentaStats {
        private final int totalVentas;
        private final int cantidadTotalVendida;
        private final double cantidadPromedioPorVenta;
        private final java.math.BigDecimal ingresosTotales;
        private final java.math.BigDecimal ingresoPromedioPorVenta;
        private final java.math.BigDecimal precioMinimoVendido;
        private final java.math.BigDecimal precioMaximoVendido;
        private final java.math.BigDecimal precioPromedioVendido;
        private final LocalDate primeraVenta;
        private final LocalDate ultimaVenta;
        
        public ProductoVentaStats(int totalVentas, int cantidadTotalVendida, double cantidadPromedioPorVenta,
                                 java.math.BigDecimal ingresosTotales, java.math.BigDecimal ingresoPromedioPorVenta,
                                 java.math.BigDecimal precioMinimoVendido, java.math.BigDecimal precioMaximoVendido,
                                 java.math.BigDecimal precioPromedioVendido, LocalDate primeraVenta, LocalDate ultimaVenta) {
            this.totalVentas = totalVentas;
            this.cantidadTotalVendida = cantidadTotalVendida;
            this.cantidadPromedioPorVenta = cantidadPromedioPorVenta;
            this.ingresosTotales = ingresosTotales;
            this.ingresoPromedioPorVenta = ingresoPromedioPorVenta;
            this.precioMinimoVendido = precioMinimoVendido;
            this.precioMaximoVendido = precioMaximoVendido;
            this.precioPromedioVendido = precioPromedioVendido;
            this.primeraVenta = primeraVenta;
            this.ultimaVenta = ultimaVenta;
        }
        
        // Getters
        public int getTotalVentas() { return totalVentas; }
        public int getCantidadTotalVendida() { return cantidadTotalVendida; }
        public double getCantidadPromedioPorVenta() { return cantidadPromedioPorVenta; }
        public java.math.BigDecimal getIngresosTotales() { return ingresosTotales; }
        public java.math.BigDecimal getIngresoPromedioPorVenta() { return ingresoPromedioPorVenta; }
        public java.math.BigDecimal getPrecioMinimoVendido() { return precioMinimoVendido; }
        public java.math.BigDecimal getPrecioMaximoVendido() { return precioMaximoVendido; }
        public java.math.BigDecimal getPrecioPromedioVendido() { return precioPromedioVendido; }
        public LocalDate getPrimeraVenta() { return primeraVenta; }
        public LocalDate getUltimaVenta() { return ultimaVenta; }
        
        // Métodos calculados
        public double getIngresoPromedioPorUnidad() {
            if (cantidadTotalVendida > 0 && ingresosTotales != null) {
                return ingresosTotales.divide(java.math.BigDecimal.valueOf(cantidadTotalVendida), 
                                            2, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            return 0.0;
        }
        
        public long getDiasEnVenta() {
            if (primeraVenta != null && ultimaVenta != null) {
                return java.time.temporal.ChronoUnit.DAYS.between(primeraVenta, ultimaVenta) + 1;
            }
            return 0;
        }
        
        public double getVentasPromedioPorDia() {
            long diasEnVenta = getDiasEnVenta();
            return diasEnVenta > 0 ? (double) totalVentas / diasEnVenta : 0.0;
        }
        
        public boolean tieneVariacionPrecios() {
            return precioMinimoVendido != null && precioMaximoVendido != null && 
                   precioMinimoVendido.compareTo(precioMaximoVendido) != 0;
        }
        
        @Override
        public String toString() {
            return String.format(
                "ProductoVentaStats{ventas=%d, cantidadVendida=%d, ingresosTotales=%s, " +
                "precioPromedio=%s, primeraVenta=%s, ultimaVenta=%s}",
                totalVentas, cantidadTotalVendida, ingresosTotales, 
                precioPromedioVendido, primeraVenta, ultimaVenta
            );
        }
    }
}

 