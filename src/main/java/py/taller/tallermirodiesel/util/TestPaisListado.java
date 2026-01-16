/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.util;

/**
 *
 * @author elyrr
 */

import py.taller.tallermirodiesel.dao.PaisDAO;
import py.taller.tallermirodiesel.dao.PaisDAOImpl;
import py.taller.tallermirodiesel.model.Pais;

public class TestPaisListado {

    public static void main(String[] args) {

        PaisDAO dao = new PaisDAOImpl();

        System.out.println("=== PAISES ACTIVOS ===");
        for (Pais p : dao.listarActivos()) {
            System.out.println(
                p.getIdPais() + " - " + p.getNombre() + " (" + p.isActivo() + ")"
            );
        }

        System.out.println("\n=== PAISES INACTIVOS ===");
        for (Pais p : dao.listarInactivos()) {
            System.out.println(
                p.getIdPais() + " - " + p.getNombre() + " (" + p.isActivo() + ")"
            );
        }
    }
}
