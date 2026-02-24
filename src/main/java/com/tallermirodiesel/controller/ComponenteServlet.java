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
import com.tallermirodiesel.dao.MarcaDAO;
import com.tallermirodiesel.dao.ModeloDAO;
import com.tallermirodiesel.dao.TipoComponenteDAO;
import com.tallermirodiesel.dao.impl.MarcaDAOImpl;
import com.tallermirodiesel.dao.impl.ModeloDAOImpl;
import com.tallermirodiesel.dao.impl.TipoComponenteDAOImpl;
import com.tallermirodiesel.model.Componente;
import com.tallermirodiesel.service.ComponenteService;
import com.tallermirodiesel.service.impl.ComponenteServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "ComponenteServlet", urlPatterns = {"/componentes"})
public class ComponenteServlet extends HttpServlet {

    private ComponenteService componenteService;
    private TipoComponenteDAO tipoComponenteDAO;
    private MarcaDAO marcaDAO;
    private ModeloDAO modeloDAO;

    // Inicialización de servicios y DAOs necesarios para las relaciones del componente
    @Override
    public void init() {
        this.componenteService  = new ComponenteServiceImpl();
        this.tipoComponenteDAO  = new TipoComponenteDAOImpl();
        this.marcaDAO           = new MarcaDAOImpl();
        this.modeloDAO          = new ModeloDAOImpl();
    }

    // Gestión de peticiones GET para navegación, edición y cambio de estado
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

    // Procesamiento de persistencia de datos vía POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            switch (action) {
                case "guardar" -> guardar(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/componentes?action=listar");
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            reenviarFormularioConDatos(req, resp);
        }
    }

    // Recuperación de componentes con soporte para búsqueda por texto
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filtro = req.getParameter("filtro");

        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("componentes", componenteService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("componentes", componenteService.listarTodos());
            req.setAttribute("filtro", "");
        }

        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/componentes/componente_listar.jsp").forward(req, resp);
    }

    // Preparación del contexto relacional para la creación de un nuevo componente
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        cargarDatosFormulario(req);
        req.setAttribute("componente", new Componente());
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/componentes/componente_form.jsp").forward(req, resp);
    }

    // Recuperación de la entidad y preparación de listas para el modo edición
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Componente> c = componenteService.buscarPorId(id);

        if (c.isEmpty()) {
            throw new IllegalArgumentException("No existe un componente con id: " + id);
        }

        cargarDatosFormulario(req);
        req.setAttribute("componente", c.get());
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/componentes/componente_form.jsp").forward(req, resp);
    }

    // Lógica para habilitar un componente en el sistema
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));
        if (id == null) throw new IllegalArgumentException("ID inválido");
        componenteService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/componentes?action=listar");
    }

    // Lógica para inhabilitar un componente en el sistema
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));
        if (id == null) throw new IllegalArgumentException("ID inválido");
        componenteService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/componentes?action=listar");
    }

    // Persistencia de la entidad componente y redirección al listado maestro
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Componente c = construirDesdeRequest(req);

        if (c.getIdComponente() == null) {
            componenteService.crear(c);
        } else {
            componenteService.actualizar(c);
        }

        resp.sendRedirect(req.getContextPath() + "/componentes?action=listar");
    }

    // Utilidad para el mapeo de parámetros HTTP hacia la entidad Componente
    private Componente construirDesdeRequest(HttpServletRequest req) {
        Componente c = new Componente();
        c.setIdComponente(parseLong(req.getParameter("idComponente")));
        c.setIdTipoComponente(parseLong(req.getParameter("idTipoComponente")));
        c.setIdMarca(parseLong(req.getParameter("idMarca")));
        c.setIdModelo(parseLong(req.getParameter("idModelo")));
        c.setNumeroSerie(req.getParameter("numeroSerie"));
        c.setObservaciones(req.getParameter("observaciones"));

        if (c.getIdComponente() != null) {
            c.setActivo("true".equals(req.getParameter("activo")));
        }

        return c;
    }

    // Recuperación de datos y listas ante fallos de validación en el servidor
    private void reenviarFormularioConDatos(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        cargarDatosFormulario(req);
        req.setAttribute("componente", construirDesdeRequest(req));
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/componentes/componente_form.jsp").forward(req, resp);
    }

    // Inyección de listas de catálogos activos para los selectores del formulario
    private void cargarDatosFormulario(HttpServletRequest req) {
        req.setAttribute("tiposComponente", tipoComponenteDAO.listarActivos());
        req.setAttribute("marcas", marcaDAO.listarActivos());
        req.setAttribute("modelos", modeloDAO.listarActivos());
    }

    // Utilidad para la conversión segura de cadenas a identificadores Long
    private Long parseLong(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}