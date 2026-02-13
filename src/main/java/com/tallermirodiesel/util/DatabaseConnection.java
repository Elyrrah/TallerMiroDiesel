/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase mejorada para gestionar conexiones a PostgreSQL
 * Utiliza HikariCP como pool de conexiones para mejor rendimiento
 * 
 * COMPATIBLE con código existente - NO requiere cambios en DAOs
 * 
 * @author elyrr
 */
public class DatabaseConnection {
    
    private static HikariDataSource dataSource;
    private static Properties dbProperties;
    
    // Bloque estático: se ejecuta UNA SOLA VEZ cuando se carga la clase
    static {
        try {
            cargarConfiguracion();
            inicializarPool();
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando pool de conexiones: " + e.getMessage(), e);
        }
    }
    
    /**
     * Carga las propiedades del archivo config.properties
     * Se ejecuta una sola vez (Singleton)
     */
    private static void cargarConfiguracion() throws Exception {
        dbProperties = new Properties();
        try (InputStream is = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new RuntimeException("No se encontró config.properties en resources");
            }
            dbProperties.load(is);
        }
    }
    
    /**
     * Inicializa el pool de conexiones HikariCP
     * Configuración optimizada para PostgreSQL
     */
    private static void inicializarPool() {
        HikariConfig config = new HikariConfig();
        
        // Configuración básica desde properties
        config.setJdbcUrl(dbProperties.getProperty("db.url"));
        config.setUsername(dbProperties.getProperty("db.user"));
        config.setPassword(dbProperties.getProperty("db.pass"));
        config.setDriverClassName("org.postgresql.Driver");
        
        // Configuración del pool (optimizada para desarrollo/producción pequeña)
        config.setMaximumPoolSize(10);          // Máximo 10 conexiones simultáneas
        config.setMinimumIdle(2);               // Mínimo 2 conexiones en espera
        config.setConnectionTimeout(30000);     // 30 segundos timeout
        config.setIdleTimeout(600000);          // 10 minutos idle
        config.setMaxLifetime(1800000);         // 30 minutos vida máxima
        
        // Configuración adicional
        config.setPoolName("TallerMiroDiesel-Pool");
        config.setAutoCommit(true);
        
        // Test de conexión al obtenerla
        config.setConnectionTestQuery("SELECT 1");
        
        // Crear el DataSource
        dataSource = new HikariDataSource(config);
        
        System.out.println("✅ Pool de conexiones HikariCP inicializado correctamente");
    }
    
    /**
     * Obtiene una conexión del pool
     * MÉTODO COMPATIBLE - No requiere cambios en código existente
     * 
     * @return Connection activa desde el pool
     */
    public static Connection getConexion() {
        try {
            Connection conn = dataSource.getConnection();
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo conexión del pool: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cierra una conexión (la devuelve al pool)
     * Método opcional - tu código existente puede seguir usando conn.close()
     * 
     * @param conn Conexión a cerrar
     */
    public static void closeConexion(Connection conn) {
        if (conn != null) {
            try {
                conn.close(); // HikariCP intercepta esto y la devuelve al pool
            } catch (SQLException e) {
                System.err.println("Error cerrando conexión: " + e.getMessage());
            }
        }
    }
    
    /**
     * Cierra el pool de conexiones
     * Llamar solo al cerrar la aplicación
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("🔒 Pool de conexiones cerrado");
        }
    }
    
    /**
     * Obtiene estadísticas del pool (útil para debugging)
     */
    public static void printPoolStats() {
        if (dataSource != null) {
            System.out.println("📊 Estadísticas del Pool:");
            System.out.println("   - Conexiones activas: " + 
                (dataSource.getHikariPoolMXBean().getActiveConnections()));
            System.out.println("   - Conexiones idle: " + 
                (dataSource.getHikariPoolMXBean().getIdleConnections()));
            System.out.println("   - Conexiones totales: " + 
                (dataSource.getHikariPoolMXBean().getTotalConnections()));
            System.out.println("   - Threads esperando: " + 
                (dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()));
        }
    }
}
