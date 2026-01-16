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

public class TestPaisDAO {

    public static void main(String[] args) {
        PaisDAO dao = new PaisDAOImpl();

        // CAMBIA este ID por uno que exista
        Long idPrueba = 1L;

        // 1) Desactivar (soft delete)
        boolean desactivado = dao.desactivar(idPrueba);
        System.out.println("Desactivado: " + desactivado);

        // 2) Eliminar (DELETE físico) — NO EJECUTAR por ahora
        // boolean eliminado = dao.eliminar(idPrueba);
        // System.out.println("Eliminado: " + eliminado);
    }
}
