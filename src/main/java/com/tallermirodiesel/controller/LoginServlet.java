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
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import com.tallermirodiesel.model.SesionDeUsuario;
import com.tallermirodiesel.model.Usuario;
import com.tallermirodiesel.service.AutenticacionService;
import com.tallermirodiesel.service.impl.AutenticacionServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private AutenticacionService autenticacionService;

    @Override
    public void init() {
        this.autenticacionService = new AutenticacionServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "mostrar";
        }

        try {
            switch (action) {
                case "mostrar" -> mostrarLogin(req, resp);
                default        -> mostrarLogin(req, resp);
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            mostrarLogin(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "login";
        }

        try {
            switch (action) {
                case "logout" -> logout(req, resp);
                case "login"  -> login(req, resp);
                default       -> login(req, resp);
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", req.getParameter("username"));
            req.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(req, resp);
        }
    }

    // Muestra el formulario de login
    // Si ya hay sesión activa redirige directo al home
    private void mostrarLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sesion = req.getSession(false);
        if (sesion != null && sesion.getAttribute("usuarioSesion") != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(req, resp);
    }

    // Procesa las credenciales y crea la sesión si son correctas
    private void login(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 1. Verificamos las credenciales con el servicio de autenticación
        Usuario usuario = autenticacionService.login(username, password);

        // 2. Creamos el objeto de sesión a partir del usuario autenticado
        SesionDeUsuario usuarioSesion = new SesionDeUsuario(usuario);

        // 3. Creamos la sesión HTTP y guardamos el objeto de sesión
        HttpSession sesion = req.getSession(true);
        sesion.setAttribute("usuarioSesion", usuarioSesion);

        // 4. Redirigimos al home
        resp.sendRedirect(req.getContextPath() + "/home");
    }

    // Invalida la sesión activa y redirige al login
    private void logout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession sesion = req.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }

        resp.sendRedirect(req.getContextPath() + "/login");
    }
}