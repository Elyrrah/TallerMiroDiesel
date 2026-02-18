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

    @Override
    public void init() {
        this.tipoDocumentoService = new TipoDocumentoServiceImpl();
    }

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
                // CORRECCIÓN 1: "buscar" apunta directamente a listar()
                case "buscar"     -> listar(req, resp);
                case "listar"     -> listar(req, resp);
                default           -> listar(req, resp);
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            listar(req, resp);
        }
    }

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
            // CORRECCIÓN 3: reutiliza reenviarFormularioConDatos()
            reenviarFormularioConDatos(req, resp);
        }
    }

    // LISTAR (con filtro opcional por nombre).
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("tiposDocumento", tipoDocumentoService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("tiposDocumento", tipoDocumentoService.listarTodos());
            // CORRECCIÓN 2: siempre seteamos "filtro" para que el JSP nunca lo reciba como null
            req.setAttribute("filtro", "");
        }

        req.getRequestDispatcher("/WEB-INF/views/catalogos/tipos_documento/tipo_documento_listar.jsp").forward(req, resp);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("tipoDocumento", new TipoDocumento());
        req.setAttribute("aplicaAOptions", TipoDocumentoAplicaEnum.values());
        req.getRequestDispatcher("/WEB-INF/views/catalogos/tipos_documento/tipo_documento_form.jsp").forward(req, resp);
    }

    // FORMULARIO EDITAR.
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

    // ACTIVAR.
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        tipoDocumentoService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=listar");
    }

    // DESACTIVAR.
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        tipoDocumentoService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=listar");
    }

    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // CORRECCIÓN 3: reutiliza construirDesdeRequest()
        TipoDocumento td = construirDesdeRequest(req);

        if (td.getIdTipoDocumento() == null) {
            td.setActivo(true);
            tipoDocumentoService.crear(td);
        } else {
            tipoDocumentoService.actualizar(td);
        }

        resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=listar");
    }

    // CONSTRUIR TIPODOCUMENTO DESDE REQUEST.
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

    // REENVIAR FORMULARIO CON DATOS (EN CASO DE ERROR EN GUARDAR).
    private void reenviarFormularioConDatos(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("tipoDocumento", construirDesdeRequest(req));
        req.setAttribute("aplicaAOptions", TipoDocumentoAplicaEnum.values());
        req.getRequestDispatcher("/WEB-INF/views/catalogos/tipos_documento/tipo_documento_form.jsp").forward(req, resp);
    }

    // PARSEO DE LONG SEGURO (RETORNA NULL SI NO APLICA O ES INVÁLIDO).
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