/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.DevTest;

import py.taller.tallermirodiesel.util.DatabaseConnection;
import java.sql.Connection;

public class TestConexion {
    public static void main(String[] args) {
        try {
            System.out.println("🔄 Probando conexión al pool...");
            
            // Obtener conexión (igual que antes)
            Connection conn = DatabaseConnection.getConexion();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Conexión exitosa!");
                System.out.println("   Catálogo: " + conn.getCatalog());
                
                // Ver estadísticas del pool
                DatabaseConnection.printPoolStats();
                
                // Cerrar conexión
                conn.close();
                System.out.println("✅ Conexión cerrada correctamente");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}