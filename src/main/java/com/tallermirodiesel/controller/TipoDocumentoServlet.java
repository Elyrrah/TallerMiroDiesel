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
import java.util.Optional;
import com.tallermirodiesel.model.TipoDocumento;
import com.tallermirodiesel.model.enums.TipoDocumentoAplicaEnum;
import com.tallermirodiesel.service.TipoDocumentoService;
import com.tallermirodiesel.service.impl.TipoDocumentoServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "TipoDocumentoServlet", urlPatterns = {"/tipos-documento"})
public class TipoDocumentoServlet extends HttpServlet {

    private TipoDocumentoService tipoDocumentoService;

    // Inicialización del servicio de Tipos de Documento
    @Override
    public void init() {
        this.tipoDocumentoService = new TipoDocumentoServiceImpl();
    }

    // Gestión de peticiones de lectura y navegación de formularios vía GET
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "listar";
        }

        try {
            switch (action) {
                case "nuevo"      -> mostrarFormularioNuevo(req, resp);
                case "editar"     -> mostrarFormularioEditar(req, resp);
                case "activar"    -> activar(req, resp);
                case "desactivar" -> desactivar(req, resp);
                case "buscar"     -> listar(req, resp);
                case "listar"     -> listar(req, resp);
                default           -> listar(req, resp);
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            listar(req, resp);
        }
    }

    // Gestión de procesamiento de datos para la persistencia vía POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            switch (action) {
                case "guardar" -> guardar(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=listar");
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            reenviarFormularioConDatos(req, resp);
        }
    }

    // Lógica para recuperar la lista de tipos de documento con soporte de búsqueda
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("tiposDocumento", tipoDocumentoService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("tiposDocumento", tipoDocumentoService.listarTodos());
            req.setAttribute("filtro", "");
        }

        req.getRequestDispatcher("/WEB-INF/views/catalogos/tipos_documento/tipo_documento_listar.jsp").forward(req, resp);
    }

    // Preparación del objeto y despacho del formulario con opciones de aplicación (Enum)
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("tipoDocumento", new TipoDocumento());
        req.setAttribute("aplicaAOptions", TipoDocumentoAplicaEnum.values());
        req.getRequestDispatcher("/WEB-INF/views/catalogos/tipos_documento/tipo_documento_form.jsp").forward(req, resp);
    }

    // Recuperación del registro y despacho del formulario para edición
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<TipoDocumento> tipoDocumento = tipoDocumentoService.buscarPorId(id);

        if (tipoDocumento.isEmpty()) {
            throw new IllegalArgumentException("No existe un TipoDocumento con id: " + id);
        }

        req.setAttribute("tipoDocumento", tipoDocumento.get());
        req.setAttribute("aplicaAOptions", TipoDocumentoAplicaEnum.values());
        req.getRequestDispatcher("/WEB-INF/views/catalogos/tipos_documento/tipo_documento_form.jsp").forward(req, resp);
    }

    // Procesamiento de activación y redirección al listado principal
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        tipoDocumentoService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=listar");
    }

    // Procesamiento de desactivación y redirección al listado principal
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        tipoDocumentoService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=listar");
    }

    // Lógica para persistir la entidad y redirección final
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TipoDocumento td = construirDesdeRequest(req);

        if (td.getIdTipoDocumento() == null) {
            td.setActivo(true);
            tipoDocumentoService.crear(td);
        } else {
            tipoDocumentoService.actualizar(td);
        }

        resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=listar");
    }

    // Utilidad interna para el mapeo de parámetros HTTP incluyendo la conversión del Enum
    private TipoDocumento construirDesdeRequest(HttpServletRequest req) {
        TipoDocumento td = new TipoDocumento();

        Long idTipoDocumento = parseLong(req.getParameter("idTipoDocumento"));
        td.setIdTipoDocumento(idTipoDocumento);
        td.setNombre(req.getParameter("nombre"));
        td.setCodigo(req.getParameter("codigo"));

        String aplicaA = req.getParameter("aplicaA");
        if (aplicaA != null && !aplicaA.isBlank()) {
            try {
                td.setAplicaA(TipoDocumentoAplicaEnum.valueOf(aplicaA.trim().toUpperCase()));
            } catch (Exception ignored) {
            }
        }

        if (idTipoDocumento != null) {
            td.setActivo("true".equals(req.getParameter("activo")));
        }

        return td;
    }

    // Lógica de recuperación de datos y opciones ante errores de guardado
    private void reenviarFormularioConDatos(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("tipoDocumento", construirDesdeRequest(req));
        req.setAttribute("aplicaAOptions", TipoDocumentoAplicaEnum.values());
        req.getRequestDispatcher("/WEB-INF/views/catalogos/tipos_documento/tipo_documento_form.jsp").forward(req, resp);
    }

    // Utilidad interna para la conversión segura de parámetros a Long
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