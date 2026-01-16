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
import py.taller.tallermirodiesel.service.PaisService;
import py.taller.tallermirodiesel.service.PaisServiceImpl;
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

        PaisService service = new PaisServiceImpl();

        String estado = request.getParameter("estado"); // activos | inactivos | todos
        List<Pais> paises;

        if ("inactivos".equalsIgnoreCase(estado)) {
            paises = service.listarInactivos();
            request.setAttribute("titulo", "Países inactivos");
        } else if ("todos".equalsIgnoreCase(estado)) {
            paises = service.listarTodos();
            request.setAttribute("titulo", "Todos los países");
        } else {
            paises = service.listarActivos();
            request.setAttribute("titulo", "Países activos");
        }

        request.setAttribute("paises", paises);
        request.getRequestDispatcher("/WEB-INF/views/paises.jsp")
               .forward(request, response);
    }

}