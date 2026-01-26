/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package py.taller.tallermirodiesel.controller;

import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * @author elyrr
 */
@WebServlet("/") // Mapea este servlet a la raíz de la aplicación
public class HomeServlet extends HttpServlet {

    @Override // Sobrescribe el método doGet de HttpServlet para manejar peticiones HTTP GET
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Crea un dispatcher para reenviar la petición a la vista index.jsp
        RequestDispatcher dispatcher =
                request.getRequestDispatcher("/WEB-INF/views/index.jsp");

        // Reenvía la petición y la respuesta al JSP sin cambiar la URL
        dispatcher.forward(request, response);
    }
}
