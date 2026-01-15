/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.util;

/**
 *
 * @author elyrr
 */

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {

    public static Connection getConexion() {
        try {
            Properties p = new Properties();

            try (InputStream is = DatabaseConnection.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties")) {

                if (is == null) {
                    throw new RuntimeException("No se encontró config.properties en resources");
                }

                p.load(is);
            }

            String url = p.getProperty("db.url");
            String user = p.getProperty("db.user");
            String pass = p.getProperty("db.pass"); 
            
            return DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            throw new RuntimeException("Error conectando a la BD: " + e.getMessage(), e);
        }
    }
}
