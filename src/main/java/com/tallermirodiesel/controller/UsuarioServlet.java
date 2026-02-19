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
import com.tallermirodiesel.model.Rol;
import com.tallermirodiesel.model.Usuario;
import com.tallermirodiesel.service.AutenticacionService;
import com.tallermirodiesel.service.UsuarioService;
import com.tallermirodiesel.service.impl.AutenticacionServiceImpl;
import com.tallermirodiesel.service.impl.UsuarioServiceImpl;
import com.tallermirodiesel.dao.RolDAO;
import com.tallermirodiesel.dao.impl.RolDAOImpl;
import com.tallermirodiesel.dao.TipoDocumentoDAO;
import com.tallermirodiesel.dao.impl.TipoDocumentoDAOImpl;
/**
 * @author elyrr
 */
@WebServlet(name = "UsuarioServlet", urlPatterns = {"/usuarios"})
public class UsuarioServlet extends HttpServlet {

    private UsuarioService usuarioService;
    private AutenticacionService autenticacionService;
    private RolDAO rolDAO;
    private TipoDocumentoDAO tipoDocumentoDAO;

    @Override
    public void init() {
        this.usuarioService       = new UsuarioServiceImpl();
        this.autenticacionService = new AutenticacionServiceImpl();
        this.rolDAO               = new RolDAOImpl();
        this.tipoDocumentoDAO     = new TipoDocumentoDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "listar";
        }

        try {
            switch (action) {
                case "listar"          -> listar(req, resp);
                case "buscar"          -> listar(req, resp);
                case "nuevo"           -> mostrarFormularioNuevo(req, resp);
                case "editar"          -> mostrarFormularioEditar(req, resp);
                case "activar"         -> activar(req, resp);
                case "desactivar"      -> desactivar(req, resp);
                case "cambiarPassword" -> mostrarCambiarPassword(req, resp);
                default                -> listar(req, resp);
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            listar(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            switch (action) {
                case "guardar"         -> guardar(req, resp);
                case "cambiarPassword" -> cambiarPassword(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/usuarios?action=listar");
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            listar(req, resp);
        }
    }

    // Lista todos los usuarios con filtro opcional por nombre o username
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String filtro = req.getParameter("filtro");

        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("usuarios", usuarioService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("usuarios", usuarioService.listarTodos());
            req.setAttribute("filtro", "");
        }

        req.getRequestDispatcher("/WEB-INF/views/sistema/usuarios/usuario_listar.jsp").forward(req, resp);
    }

    // Muestra el formulario para crear un nuevo usuario
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("usuario", new Usuario());
        req.setAttribute("roles", rolDAO.listarActivos());
        req.setAttribute("tiposDocumento", tipoDocumentoDAO.listarActivos());
        req.getRequestDispatcher("/WEB-INF/views/sistema/usuarios/usuario_form.jsp").forward(req, resp);
    }

    // Muestra el formulario para editar un usuario existente
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new RuntimeException("ID inválido.");
        }

        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con id: " + id));

        req.setAttribute("usuario", usuario);
        req.setAttribute("roles", rolDAO.listarActivos());
        req.setAttribute("tiposDocumento", tipoDocumentoDAO.listarActivos());
        req.getRequestDispatcher("/WEB-INF/views/sistema/usuarios/usuario_form.jsp").forward(req, resp);
    }

    // Muestra el formulario para cambiar la contraseña de un usuario
    private void mostrarCambiarPassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new RuntimeException("ID inválido.");
        }

        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("No existe un usuario con id: " + id));

        req.setAttribute("usuario", usuario);
        req.getRequestDispatcher("/WEB-INF/views/sistema/usuarios/usuario_cambiar_password.jsp").forward(req, resp);
    }

    // Activa un usuario y vuelve al listado
    private void activar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new RuntimeException("ID inválido.");
        }

        usuarioService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/usuarios?action=listar");
    }

    // Desactiva un usuario y vuelve al listado
    private void desactivar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new RuntimeException("ID inválido.");
        }

        usuarioService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/usuarios?action=listar");
    }

    // Crea o actualiza un usuario según si viene o no el idUsuario
    private void guardar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        Long idUsuario        = parseLong(req.getParameter("idUsuario"));
        String nombre         = req.getParameter("nombre");
        String apellido       = req.getParameter("apellido");
        Long idTipoDocumento  = parseLong(req.getParameter("idTipoDocumento"));
        String numeroDoc      = req.getParameter("numeroDocumento");
        String email          = req.getParameter("email");
        String telefono       = req.getParameter("telefono");
        Long idRol            = parseLong(req.getParameter("idRol"));

        // Construimos el objeto Rol con el id seleccionado
        Rol rol = new Rol();
        rol.setIdRol(idRol);

        // Construimos el objeto Usuario
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setIdTipoDocumento(idTipoDocumento);
        usuario.setNumeroDocumento(numeroDoc);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setRol(rol);

        if (idUsuario == null) {
            // CREAR — tomamos username y password del formulario
            usuario.setUsername(req.getParameter("username"));
            usuario.setActivo(true);
            usuarioService.crear(usuario, req.getParameter("password"));
        } else {
            // ACTUALIZAR — tomamos el campo activo del formulario
            usuario.setActivo("true".equals(req.getParameter("activo")));
            usuarioService.actualizar(usuario);
        }

        resp.sendRedirect(req.getContextPath() + "/usuarios?action=listar");
    }

    // Cambia la contraseña de un usuario verificando la actual primero
    private void cambiarPassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long idUsuario         = parseLong(req.getParameter("idUsuario"));
        String passwordActual  = req.getParameter("passwordActual");
        String passwordNueva   = req.getParameter("passwordNueva");
        String passwordConfirm = req.getParameter("passwordConfirmar");

        // Verificamos que las nuevas contraseñas coincidan
        if (passwordNueva == null || !passwordNueva.equals(passwordConfirm)) {
            Usuario usuario = usuarioService.buscarPorId(idUsuario).orElse(new Usuario());
            req.setAttribute("usuario", usuario);
            req.setAttribute("error", "La nueva contraseña y su confirmación no coinciden.");
            req.getRequestDispatcher("/WEB-INF/views/sistema/usuarios/usuario_cambiar_password.jsp").forward(req, resp);
            return;
        }

        // Delegamos al servicio de autenticación que verifica la contraseña actual
        autenticacionService.cambiarPassword(idUsuario, passwordActual, passwordNueva);

        // Invalidamos la sesión y redirigimos al login
        req.getSession(false).invalidate();
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}