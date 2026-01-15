/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package py.taller.tallermirodiesel.controller;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import py.taller.tallermirodiesel.dao.PaisDAO;
import py.taller.tallermirodiesel.dao.PaisDAOImpl;
import py.taller.tallermirodiesel.model.Pais;

/**
 *
 * @author elyrr
 */
@WebServlet("/paises")
public class PaisServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PaisDAO dao = new PaisDAOImpl();
        List<Pais> paises = dao.listarTodos();

        request.setAttribute("paises", paises);
        request.getRequestDispatcher("/WEB-INF/views/paises.jsp")
               .forward(request, response);
    }
}