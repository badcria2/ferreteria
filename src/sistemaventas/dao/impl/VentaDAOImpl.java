/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dao.impl;

import sistemaventas.dao.interfaces.IVentaDAO;
import sistemaventas.entity.Venta;
import sistemaventas.util.ConnectionUtil;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import sistemaventas.entity.Cliente;

/**
 * Implementación del DAO para la gestión de ventas
 */
public class VentaDAOImpl implements IVentaDAO {
    
    @Override
    public Venta create(Venta venta) throws Exception {
        String sql = "INSERT INTO Ventas (fecha, idCliente, total, estado) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setDate(1, Date.valueOf(venta.getFecha()));
            ps.setInt(2, venta.getIdCliente());
            ps.setBigDecimal(3, venta.getTotal());
            ps.setString(4, venta.getEstado().name());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear venta, no se insertó ninguna fila.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    venta.setIdVentas(generatedKeys.getInt(1));
                }
            }
            
            return venta;
        }
    }
    
    @Override
    public Optional<Venta> findById(Integer id) throws Exception {
        String sql = "SELECT v.*, c.nombre as cliente_nombre, c.NroDocumento as cliente_documento, c.correo " +
                     "FROM Ventas v " +
                     "JOIN Clientes c ON v.idCliente = c.idCliente " +
                     "WHERE v.idVentas = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVenta(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public List<Venta> findAll() throws Exception {
        String sql = "SELECT v.*, c.nombre as cliente_nombre, c.NroDocumento as cliente_documento, c.correo " +
                     "FROM Ventas v " +
                     "JOIN Clientes c ON v.idCliente = c.idCliente " +
                     "ORDER BY v.fecha DESC, v.idVentas DESC";
        
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ventas.add(mapResultSetToVenta(rs));
            }
        }
        return ventas;
    }
    
    @Override
    public Venta update(Venta venta) throws Exception {
        String sql = "UPDATE Ventas SET fecha = ?, idCliente = ?, total = ?, estado = ? WHERE idVentas = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(venta.getFecha()));
            ps.setInt(2, venta.getIdCliente());
            ps.setBigDecimal(3, venta.getTotal());
            ps.setString(4, venta.getEstado().name());
            ps.setInt(5, venta.getIdVentas());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar venta, venta no encontrada.");
            }
            
            return venta;
        }
    }
    
    @Override
    public boolean delete(Integer id) throws Exception {
        String sql = "UPDATE Ventas SET estado = 'CANCELADA' WHERE idVentas = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    @Override
    public long count() throws Exception {
        String sql = "SELECT COUNT(*) FROM Ventas WHERE estado != 'CANCELADA'";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }
    
    @Override
    public List<Venta> findByCliente(Integer idCliente) throws Exception {
        String sql = "SELECT v.*, c.nombre as cliente_nombre, c.NroDocumento as cliente_documento, c.correo " +
                     "FROM Ventas v " +
                     "JOIN Clientes c ON v.idCliente = c.idCliente " +
                     "WHERE v.idCliente = ? " +
                     "ORDER BY v.fecha DESC";
        
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idCliente);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapResultSetToVenta(rs));
                }
            }
        }
        return ventas;
    }
    
    @Override
    public List<Venta> findByFecha(LocalDate fecha) throws Exception {
        String sql = "SELECT v.*, c.nombre as cliente_nombre, c.NroDocumento as cliente_documento , c.correo" +
                     "FROM Ventas v " +
                     "JOIN Clientes c ON v.idCliente = c.idCliente " +
                     "WHERE v.fecha = ? " +
                     "ORDER BY v.idVentas DESC";
        
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(fecha));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapResultSetToVenta(rs));
                }
            }
        }
        return ventas;
    }
    
    @Override
    public List<Venta> findByFechaRange(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT v.*, c.nombre as cliente_nombre, c.NroDocumento as cliente_documento, c.correo " +
                     "FROM Ventas v " +
                     "JOIN Clientes c ON v.idCliente = c.idCliente " +
                     "WHERE v.fecha BETWEEN ? AND ? " +
                     "ORDER BY v.fecha DESC, v.idVentas DESC";
        
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapResultSetToVenta(rs));
                }
            }
        }
        return ventas;
    }
    
    @Override
    public List<Venta> findByEstado(Venta.EstadoVenta estado) throws Exception {
        String sql = "SELECT v.*, c.nombre as cliente_nombre, c.NroDocumento as cliente_documento , c.correo" +
                     "FROM Ventas v " +
                     "JOIN Clientes c ON v.idCliente = c.idCliente " +
                     "WHERE v.estado = ? " +
                     "ORDER BY v.fecha DESC";
        
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, estado.name());
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapResultSetToVenta(rs));
                }
            }
        }
        return ventas;
    }
    
    @Override
    public List<Venta> findVentasHoy() throws Exception {
        return findByFecha(LocalDate.now());
    }
    
    @Override
    public List<Venta> findVentasMesActual() throws Exception {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
        
        return findByFechaRange(inicioMes, finMes);
    }
    
    @Override
    public List<Venta> findVentasRecientes(int limite) throws Exception {
        String sql = "SELECT v.*, c.nombre as cliente_nombre, c.NroDocumento as cliente_documento, c.correo " +
                     "FROM Ventas v " +
                     "JOIN Clientes c ON v.idCliente = c.idCliente " +
                     "ORDER BY v.fecha_registro DESC " +
                     "LIMIT ?";
        
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, limite);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapResultSetToVenta(rs));
                }
            }
        }
        return ventas;
    }
    
    @Override
    public BigDecimal calcularTotalVentas(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT SUM(total) as total_ventas " +
                     "FROM Ventas " +
                     "WHERE fecha BETWEEN ? AND ? AND estado = 'COMPLETADA'";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total_ventas");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public long countByEstado(Venta.EstadoVenta estado) throws Exception {
        String sql = "SELECT COUNT(*) FROM Ventas WHERE estado = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, estado.name());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }
    
    private Venta mapResultSetToVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setIdVentas(rs.getInt("idVentas"));
        venta.setFecha(rs.getDate("fecha").toLocalDate());
        venta.setIdCliente(rs.getInt("idCliente"));
        venta.setTotal(rs.getBigDecimal("total"));
        venta.setEstado(Venta.EstadoVenta.valueOf(rs.getString("estado")));
        
        Cliente c =  new Cliente(rs.getString("cliente_nombre"), rs.getString("cliente_documento"),  rs.getString("correo"));
        venta.setCliente(c);
        Timestamp timestamp = rs.getTimestamp("fecha_registro");
        if (timestamp != null) {
            venta.setFechaRegistro(timestamp.toLocalDateTime());
        }
        
        return venta;
    }
}
