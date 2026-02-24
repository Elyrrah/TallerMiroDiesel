/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.tallermirodiesel.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author elyrr
 */
// Define el endpoint "/acceso-denegado" al que serán redirigidos los usuarios sin permisos
@WebServlet(name = "AccesoDenegadoServlet", urlPatterns = {"/acceso-denegado"})
public class AccesoDenegadoServlet extends HttpServlet {

    @Override
    public void init() {
        // Inicialización del servlet (sin dependencias externas en este caso)
    }

    // Procesa solicitudes GET para mostrar la vista de error de permisos
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Redirige internamente a la ubicación protegida del JSP de error
        req.getRequestDispatcher("/WEB-INF/views/error/acceso_denegado.jsp").forward(req, resp);
    }

    // Gestiona solicitudes POST delegándolas al método doGet para mostrar la misma vista
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Asegura que incluso si el bloqueo ocurre tras enviar un formulario, se muestre la página de error
        doGet(req, resp);
    }
}