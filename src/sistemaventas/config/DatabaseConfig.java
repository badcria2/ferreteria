/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemaventas.config;

public class DatabaseConfig {
    public static final String DB_URL = "jdbc:mysql://127.0.0.1/sistema_ventas";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "85857855xD123";
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Pool de conexiones
    public static final int MAX_CONNECTIONS = 10;
    public static final int INITIAL_CONNECTIONS = 5;
    public static final long CONNECTION_TIMEOUT = 30000; // 30 segundos
}
