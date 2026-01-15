/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.util;

import java.util.List;
import py.taller.tallermirodiesel.dao.PaisDAO;
import py.taller.tallermirodiesel.dao.PaisDAOImpl;
import py.taller.tallermirodiesel.model.Pais;

/**
 *
 * @author elyrr
 */

public class TestPaisDAO {

    public static void main(String[] args) {
        PaisDAO dao = new PaisDAOImpl();
        List<Pais> paises = dao.listarTodos();

        System.out.println("Total paises: " + paises.size());
        for (Pais p : paises) {
            System.out.println(
                    p.getIdPais() + " | " +
                    p.getNombre() + " | " +
                    p.getIso2() + " | " +
                    p.getIso3() + " | activo=" + p.isActivo()
            );
        }
    }
}
