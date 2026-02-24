/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import com.tallermirodiesel.model.Pais;
import com.tallermirodiesel.service.PaisService;
import com.tallermirodiesel.service.impl.PaisServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "PaisServlet", urlPatterns = {"/paises"})
public class PaisServlet extends HttpServlet {

    private PaisService paisService;

    // Inicialización del servicio de países al cargar el servlet en el contenedor
    @Override
    public void init() {
        this.paisService = new PaisServiceImpl();
    }

    // Gestión de peticiones de lectura, navegación de formularios y cambios de estado vía GET
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

    // Gestión de procesamiento de datos enviados desde formularios para persistencia vía POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            switch (action) {
                case "guardar" -> guardar(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/paises?action=listar");
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());

            Pais p = new Pais();
            Long idPais = parseLong(req.getParameter("idPais"));
            p.setIdPais(idPais);
            p.setNombre(req.getParameter("nombre"));
            p.setIso2(req.getParameter("iso2"));
            p.setIso3(req.getParameter("iso3"));
            p.setActivo("true".equals(req.getParameter("activo")));

            req.setAttribute("error", e.getMessage());
            req.setAttribute("pais", p);
            req.getRequestDispatcher("/WEB-INF/views/geografia/paises/pais_form.jsp").forward(req, resp);
        }
    }

    // Lógica para recuperar la lista de países y despachar la vista principal de listado
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("paises", paisService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("paises", paisService.listarTodos());
            req.setAttribute("filtro", "");
        }

        req.getRequestDispatcher("/WEB-INF/views/geografia/paises/pais_listar.jsp").forward(req, resp);
    }

    // Preparación de un objeto vacío y despacho del formulario para registro de un nuevo país
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("pais", new Pais());
        req.getRequestDispatcher("/WEB-INF/views/geografia/paises/pais_form.jsp").forward(req, resp);
    }

    // Recuperación de datos existentes y despacho del formulario en modo edición
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Pais> pais = paisService.buscarPorId(id);

        if (pais.isEmpty()) {
            throw new IllegalArgumentException("No existe un país con id: " + id);
        }

        req.setAttribute("pais", pais.get());
        req.getRequestDispatcher("/WEB-INF/views/geografia/paises/pais_form.jsp").forward(req, resp);
    }

    // Procesamiento de la solicitud para habilitar un país y redirección al listado actualizado
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        paisService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/paises?action=listar");
    }

    // Procesamiento de la solicitud para inhabilitar un país y redirección al listado actualizado
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        paisService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/paises?action=listar");
    }

    // Lógica para recolectar datos del formulario, determinar operación (crear/actualizar) y persistir
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Pais p = new Pais();

        Long idPais = parseLong(req.getParameter("idPais"));
        p.setIdPais(idPais);
        p.setNombre(req.getParameter("nombre"));
        p.setIso2(req.getParameter("iso2"));
        p.setIso3(req.getParameter("iso3"));

        if (idPais == null) {
            p.setActivo(true);
            paisService.crear(p);
        } else {
            p.setActivo("true".equals(req.getParameter("activo")));
            paisService.actualizar(p);
        }

        resp.sendRedirect(req.getContextPath() + "/paises?action=listar");
    }

    // Utilidad interna para la conversión segura de cadenas de texto a identificadores numéricos
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