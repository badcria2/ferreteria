/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import sistemaventas.config.DatabaseConfig;

public class ConnectionUtil {
    
    private static ConnectionUtil instance;
    private BlockingQueue<Connection> connectionPool;
    private boolean initialized = false;
    
    private ConnectionUtil() {
        initializePool();
    }
    
    public static synchronized ConnectionUtil getInstance() {
        if (instance == null) {
            instance = new ConnectionUtil();
        }
        return instance;
    }
    
    private void initializePool() {
        try {
            Class.forName(DatabaseConfig.DB_DRIVER);
            connectionPool = new ArrayBlockingQueue<>(DatabaseConfig.MAX_CONNECTIONS);
            
            // Crear conexiones iniciales
            for (int i = 0; i < DatabaseConfig.INITIAL_CONNECTIONS; i++) {
                Connection connection = createNewConnection();
                connectionPool.offer(connection);
            }
            
            initialized = true;
            System.out.println("Pool de conexiones inicializado con " + DatabaseConfig.INITIAL_CONNECTIONS + " conexiones");
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error al inicializar el pool de conexiones: " + e.getMessage());
            throw new RuntimeException("No se pudo inicializar el pool de conexiones", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return getInstance().borrowConnection();
    }
    
    private Connection borrowConnection() throws SQLException {
        if (!initialized) {
            throw new SQLException("Pool de conexiones no inicializado");
        }
        
        try {
            Connection connection = connectionPool.poll();
            
            if (connection == null || connection.isClosed() || !connection.isValid(5)) {
                connection = createNewConnection();
            }
            
            return connection;
            
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión: " + e.getMessage());
            throw e;
        }
    }
    
    public static void returnConnection(Connection connection) {
        getInstance().returnConnectionToPool(connection);
    }
    
    private void returnConnectionToPool(Connection connection) {
        if (connection != null && connectionPool.size() < DatabaseConfig.MAX_CONNECTIONS) {
            try {
                if (!connection.isClosed() && connection.isValid(5)) {
                    connectionPool.offer(connection);
                } else {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al retornar conexión al pool: " + e.getMessage());
            }
        }
    }
    
    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(
            DatabaseConfig.DB_URL,
            DatabaseConfig.DB_USER,
            DatabaseConfig.DB_PASSWORD
        );
    }
    
    public void closeAllConnections() {
        while (!connectionPool.isEmpty()) {
            try {
                Connection connection = connectionPool.poll();
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
        System.out.println("Todas las conexiones del pool han sido cerradas");
    }
}