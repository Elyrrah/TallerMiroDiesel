/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.tallermirodiesel.controller;

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
// Define este Servlet y lo vincula tanto a la raíz ("") como a la ruta "/home"
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {
    
    // Procesa las solicitudes de tipo GET enviadas al servidor
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Define la ruta interna del archivo JSP que servirá como página de inicio
        String viewPath = "/WEB-INF/views/index/index.jsp";
        
        // Crea el despachador encargado de transferir el control a la vista especificada
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        
        // Ejecuta el reenvío de la solicitud y la respuesta hacia el JSP de forma interna
        dispatcher.forward(request, response);
    }
}