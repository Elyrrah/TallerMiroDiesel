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
import java.util.List;
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
                case "nuevo" -> mostrarFormularioNuevo(req, resp);
                case "editar" -> mostrarFormularioEditar(req, resp);
                case "activar" -> activar(req, resp);
                case "desactivar" -> desactivar(req, resp);
                case "buscar" -> buscar(req, resp);
                case "listar" -> listar(req, resp);
                default -> listar(req, resp);
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
            req.setAttribute("aplicaAOptions", TipoDocumentoAplicaEnum.values());

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

            td.setActivo("true".equals(req.getParameter("activo")));

            req.setAttribute("tipoDocumento", td);
            req.getRequestDispatcher("/WEB-INF/views/tipos_documento/tipo_documento_form.jsp").forward(req, resp);
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("tiposDocumento", tipoDocumentoService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("tiposDocumento", tipoDocumentoService.listarTodos());
        }

        req.getRequestDispatcher("/WEB-INF/views/tipos_documento/tipo_documento_listar.jsp").forward(req, resp);
    }

    private void buscar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        List<TipoDocumento> lista;
        if (filtro == null || filtro.isBlank()) {
            lista = tipoDocumentoService.listarTodos();
        } else {
            lista = tipoDocumentoService.buscarPorNombreParcial(filtro);
        }

        req.setAttribute("tiposDocumento", lista);
        req.setAttribute("filtro", (filtro == null) ? "" : filtro);
        req.getRequestDispatcher("/WEB-INF/views/tipos_documento/tipo_documento_listar.jsp").forward(req, resp);
    }

    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("tipoDocumento", new TipoDocumento());
        req.setAttribute("aplicaAOptions", TipoDocumentoAplicaEnum.values());
        req.getRequestDispatcher("/WEB-INF/views/tipos_documento/tipo_documento_form.jsp").forward(req, resp);
    }

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
        req.getRequestDispatcher("/WEB-INF/views/tipos_documento/tipo_documento_form.jsp").forward(req, resp);
    }

    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        tipoDocumentoService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=listar");
    }

    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        tipoDocumentoService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=listar");
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        TipoDocumento td = new TipoDocumento();

        Long idTipoDocumento = parseLong(req.getParameter("idTipoDocumento"));
        td.setIdTipoDocumento(idTipoDocumento);
        td.setNombre(req.getParameter("nombre"));
        td.setCodigo(req.getParameter("codigo"));

        String aplicaA = req.getParameter("aplicaA");
        if (aplicaA != null && !aplicaA.isBlank()) {
            td.setAplicaA(TipoDocumentoAplicaEnum.valueOf(aplicaA.trim().toUpperCase()));
        }

        if (idTipoDocumento == null) {
            td.setActivo(true);
            tipoDocumentoService.crear(td);
        } else {
            td.setActivo("true".equals(req.getParameter("activo")));
            tipoDocumentoService.actualizar(td);
        }

        resp.sendRedirect(req.getContextPath() + "/tipos-documento?action=listar");
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