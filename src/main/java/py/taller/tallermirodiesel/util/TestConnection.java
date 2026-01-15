/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.util;

import java.sql.Connection;

/**
 *
 * @author elyrr
 */
public class TestConnection {
        public static void main(String[] args) {
        try (Connection con = DatabaseConnection.getConexion()) {
            System.out.println("Conexión OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
