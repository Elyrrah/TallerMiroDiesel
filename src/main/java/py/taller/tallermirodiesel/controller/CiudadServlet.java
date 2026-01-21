/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.taller.tallermirodiesel.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import py.taller.tallermirodiesel.model.Ciudad;
import py.taller.tallermirodiesel.service.CiudadService;
import py.taller.tallermirodiesel.service.impl.CiudadServiceImpl;

// Nota: Usamos el DepartamentoService para poblar el combo/select de departamentos.
// Si en tu proyecto el package o nombres difieren, ajusta los imports a tu estructura real.
import py.taller.tallermirodiesel.model.Departamento;
import py.taller.tallermirodiesel.service.DepartamentoService;
import py.taller.tallermirodiesel.service.impl.DepartamentoServiceImpl;
/**
 * @author elyrr
 */
@WebServlet(name = "CiudadServlet", urlPatterns = {"/ciudades"})
public class CiudadServlet extends HttpServlet {

    // Service utilizado por el Servlet para aplicar validaciones y reglas de negocio.
    private final CiudadService ciudadService = new CiudadServiceImpl();

    // Service utilizado para cargar departamentos (combo en el formulario y/o filtro en listado).
    private final DepartamentoService departamentoService = new DepartamentoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null || accion.isBlank()) {
            accion = "listar";
        }

        try {
            switch (accion) {
                case "nuevo" -> nuevo(request, response);
                case "editar" -> editar(request, response);
                case "activar" -> activar(request, response);
                case "desactivar" -> desactivar(request, response);
                case "listar" -> listar(request, response);
                default -> listar(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            listar(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null || accion.isBlank()) {
            accion = "guardar";
        }

        try {
            switch (accion) {
                case "guardar" -> guardar(request, response);
                default -> guardar(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());

            // Re-armar objeto para no perder datos del usuario
            Ciudad ciudad = construirDesdeRequest(request);
            request.setAttribute("ciudad", ciudad);

            // Cargar combo de departamentos
            cargarDepartamentos(request);

            // RUTA CORREGIDA: carpeta /ciudades/
            request.getRequestDispatcher("/WEB-INF/views/ciudades/ciudad_form.jsp").forward(request, response);
        }
    }

    // =========================
    // Acciones
    // =========================

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Filtro opcional por departamento
        String idDepartamentoParam = request.getParameter("idDepartamento");

        List<Ciudad> lista;
        if (idDepartamentoParam != null && !idDepartamentoParam.isBlank()) {
            Long idDepartamento = Long.valueOf(idDepartamentoParam);
            lista = ciudadService.listarPorDepartamento(idDepartamento);
            request.setAttribute("idDepartamento", idDepartamento);
        } else {
            lista = ciudadService.listarTodos();
        }

        request.setAttribute("lista", lista);

        // Cargar departamentos para filtro/combos en la vista
        cargarDepartamentos(request);

        // RUTA CORREGIDA: carpeta /ciudades/ y archivo ciudad_listar.jsp
        request.getRequestDispatcher("/WEB-INF/views/ciudades/ciudad_listar.jsp").forward(request, response);
    }

    private void nuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("ciudad", new Ciudad());
        cargarDepartamentos(request);

        // RUTA CORREGIDA: carpeta /ciudades/
        request.getRequestDispatcher("/WEB-INF/views/ciudades/ciudad_form.jsp").forward(request, response);
    }

    private void editar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = Long.valueOf(request.getParameter("id"));
        Optional<Ciudad> ciudadOpt = ciudadService.buscarPorId(id);

        if (ciudadOpt.isEmpty()) {
            throw new IllegalArgumentException("No existe una ciudad con id: " + id);
        }

        request.setAttribute("ciudad", ciudadOpt.get());
        cargarDepartamentos(request);

        // RUTA CORREGIDA: carpeta /ciudades/
        request.getRequestDispatcher("/WEB-INF/views/ciudades/ciudad_form.jsp").forward(request, response);
    }

    private void guardar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Ciudad ciudad = construirDesdeRequest(request);

        if (ciudad.getIdCiudad() == null) {
            ciudadService.crear(ciudad);
        } else {
            ciudadService.actualizar(ciudad);
        }

        response.sendRedirect(request.getContextPath() + "/ciudades?accion=listar");
    }

    private void activar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = Long.valueOf(request.getParameter("id"));
        ciudadService.activar(id);

        response.sendRedirect(request.getContextPath() + "/ciudades?accion=listar");
    }

    private void desactivar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = Long.valueOf(request.getParameter("id"));
        ciudadService.desactivar(id);

        response.sendRedirect(request.getContextPath() + "/ciudades?accion=listar");
    }

    // =========================
    // Helpers
    // =========================

    private Ciudad construirDesdeRequest(HttpServletRequest request) {
        Ciudad c = new Ciudad();

        String idCiudadParam = request.getParameter("idCiudad");
        if (idCiudadParam != null && !idCiudadParam.isBlank()) {
            c.setIdCiudad(Long.valueOf(idCiudadParam));
        }

        String idDepartamentoParam = request.getParameter("idDepartamento");
        if (idDepartamentoParam != null && !idDepartamentoParam.isBlank()) {
            c.setIdDepartamento(Long.valueOf(idDepartamentoParam));
        }

        c.setNombre(request.getParameter("nombre"));

        String activoParam = request.getParameter("activo");
        c.setActivo(activoParam != null);

        return c;
    }

    private void cargarDepartamentos(HttpServletRequest request) {
        List<Departamento> departamentos = departamentoService.listarActivos();
        request.setAttribute("departamentos", departamentos);
    }
}
