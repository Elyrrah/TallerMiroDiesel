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
// Mapea el servlet a la ruta /login para centralizar el acceso y salida del sistema
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private AutenticacionService autenticacionService;

    // Inicializa el servicio de autenticación encargado de la validación contra la base de datos
    @Override
    public void init() {
        this.autenticacionService = new AutenticacionServiceImpl();
    }

    // Gestiona las peticiones GET, principalmente para mostrar la interfaz de acceso
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

    // Gestiona las peticiones POST para procesar el inicio y cierre de sesión de forma segura
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
            // En caso de error, devuelve el nombre de usuario para no obligar a escribirlo de nuevo
            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", req.getParameter("username"));
            req.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(req, resp);
        }
    }

    // Valida si ya existe una sesión activa para evitar que un usuario logueado vuelva al login
    private void mostrarLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sesion = req.getSession(false);
        if (sesion != null && sesion.getAttribute("usuarioSesion") != null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(req, resp);
    }

    // Realiza el proceso de autenticación, creación de sesión y vinculación del usuario
    private void login(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Valida las credenciales a través de la capa de servicio
        Usuario usuario = autenticacionService.login(username, password);

        // Encapsula la información del usuario en un objeto específico para la sesión
        SesionDeUsuario usuarioSesion = new SesionDeUsuario(usuario);

        // Crea una nueva sesión HTTP y almacena el objeto de sesión para persistencia
        HttpSession sesion = req.getSession(true);
        sesion.setAttribute("usuarioSesion", usuarioSesion);

        // Redirige al panel principal tras un acceso exitoso
        resp.sendRedirect(req.getContextPath() + "/home");
    }

    // Finaliza la sesión actual y limpia los datos del servidor
    private void logout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession sesion = req.getSession(false);
        if (sesion != null) {
            // Destruye la sesión y todos sus atributos asociados
            sesion.invalidate();
        }

        // Redirige al usuario nuevamente a la pantalla de acceso
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}