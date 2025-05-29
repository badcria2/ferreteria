/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.dao.impl;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;  
import sistemaventas.dao.interfaces.IClienteDAO;
import sistemaventas.entity.Cliente;
import sistemaventas.util.ConnectionUtil;

/**
 * Implementación del DAO para la gestión de clientes
 */
public class ClienteDAOImpl implements IClienteDAO {
    
    @Override
    public Cliente create(Cliente cliente) throws Exception {
        String sql = "INSERT INTO Clientes (nombre, NroDocumento, correo) VALUES (?, ?, ?)";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getNroDocumento());
            ps.setString(3, cliente.getCorreo());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear cliente, no se insertó ninguna fila.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setIdCliente(generatedKeys.getInt(1));
                }
            }
            
            return cliente;
        }
    }
    
    @Override
    public Optional<Cliente> findById(Integer id) throws Exception {
        String sql = "SELECT * FROM Clientes WHERE idCliente = ? AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public List<Cliente> findAll() throws Exception {
        String sql = "SELECT * FROM Clientes WHERE activo = TRUE ORDER BY nombre";
        List<Cliente> clientes = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        }
        return clientes;
    }
    
    @Override
    public Cliente update(Cliente cliente) throws Exception {
        String sql = "UPDATE Clientes SET nombre = ?, NroDocumento = ?, correo = ? WHERE idCliente = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getNroDocumento());
            ps.setString(3, cliente.getCorreo());
            ps.setInt(4, cliente.getIdCliente());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar cliente, cliente no encontrado.");
            }
            
            return cliente;
        }
    }
    
    @Override
    public boolean delete(Integer id) throws Exception {
        String sql = "UPDATE Clientes SET activo = FALSE WHERE idCliente = ?";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    @Override
    public long count() throws Exception {
        String sql = "SELECT COUNT(*) FROM Clientes WHERE activo = TRUE";
        
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
    public Optional<Cliente> findByDocumento(String nroDocumento) throws Exception {
        String sql = "SELECT * FROM Clientes WHERE NroDocumento = ? AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, nroDocumento);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public List<Cliente> findByNombre(String nombre) throws Exception {
        String sql = "SELECT * FROM Clientes WHERE nombre LIKE ? AND activo = TRUE ORDER BY nombre";
        List<Cliente> clientes = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, "%" + nombre + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapResultSetToCliente(rs));
                }
            }
        }
        return clientes;
    }
    
    @Override
    public List<Cliente> findActivos() throws Exception {
        return findAll();
    }
    
    @Override
    public Optional<Cliente> findByCorreo(String correo) throws Exception {
        String sql = "SELECT * FROM Clientes WHERE correo = ? AND activo = TRUE";
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, correo);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCliente(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public long countActivos() throws Exception {
        return count();
    }
    
    @Override
    public List<Cliente> findByFechaRegistro(LocalDate fechaInicio, LocalDate fechaFin) throws Exception {
        String sql = "SELECT * FROM Clientes WHERE DATE(fecha_registro) BETWEEN ? AND ? AND activo = TRUE ORDER BY fecha_registro DESC";
        List<Cliente> clientes = new ArrayList<>();
        
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapResultSetToCliente(rs));
                }
            }
        }
        return clientes;
    }
    
    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("idCliente"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setNroDocumento(rs.getString("NroDocumento"));
        cliente.setCorreo(rs.getString("correo"));
        cliente.setActivo(rs.getBoolean("activo"));
        
        Timestamp timestamp = rs.getTimestamp("fecha_registro");
        if (timestamp != null) {
            cliente.setFechaRegistro(timestamp.toLocalDateTime());
        }
        
        return cliente;
    }
}