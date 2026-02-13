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
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.tallermirodiesel.model.Servicio;
import com.tallermirodiesel.service.ServicioService;
import com.tallermirodiesel.service.impl.ServicioServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "ServicioServlet", urlPatterns = {"/servicios"})
// Bloque: Mapeo del servlet (todas las acciones de Servicio entran por /servicios).
public class ServicioServlet extends HttpServlet {

    // Service usado por el controlador para ejecutar la lógica de negocio de Servicio.
    private ServicioService servicioService;

    // CICLO DE VIDA DEL SERVLET.
    @Override
    public void init() {
        this.servicioService = new ServicioServiceImpl();
    }

    // ========== ========== ========== ========== ========== 
    // MANEJO DE GET (VISTAS / ACCIONES DE NAVEGACIÓN).
    // ========== ========== ========== ========== ========== 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Lee el parámetro "action" para decidir qué caso ejecutar.
        String action = req.getParameter("action");

        // 2. Si no viene acción, se asume "listar" como comportamiento por defecto.
        if (action == null || action.isBlank()) action = "listar";

        // 3. Router de acciones GET (controlador tipo front-controller por parámetro).
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

    // ========== ========== ========== ========== ========== 
    // MANEJO DE POST (ACCIONES QUE MODIFICAN DATOS).
    // ========== ========== ========== ========== ========== 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Lee el parámetro "action" para decidir qué operación ejecutar.
        String action = req.getParameter("action");

        // 2. Si no viene acción, se define un default para evitar nulls.
        if (action == null || action.isBlank()) action = "guardar";

        // 3. Router de acciones POST.
        try {
            switch (action) {
                case "guardar" -> guardar(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/servicios?action=listar");
            }
        } catch (RuntimeException e) {

            // 4. Avisa en caso de error.
            req.setAttribute("error", e.getMessage());

            // 5. Rehidratar el objeto para no perder los datos ingresados
            Servicio servicio = new Servicio();
            Long idServicio = parseLong(req.getParameter("idServicio"));
            servicio.setIdServicio(idServicio);

            servicio.setCodigo(req.getParameter("codigo"));
            servicio.setNombre(req.getParameter("nombre"));
            servicio.setDescripcion(req.getParameter("descripcion"));
            servicio.setPrecioBase(parseBigDecimal(req.getParameter("precioBase")));

            // Nota: En edición tomamos el "activo"; en creación queda true por lógica del servlet/service.
            servicio.setActivo("true".equals(req.getParameter("activo")));

            req.setAttribute("servicio", servicio);

            // 6. Re-render del formulario correspondiente.
            req.getRequestDispatcher("/WEB-INF/views/servicios/servicio_form.jsp").forward(req, resp);
        }
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES GET
    // ========== ========== ========== 

    // LISTADO.
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 0. Lee el filtro opcional (para búsquedas automáticas por SQL).
        String filtro = req.getParameter("filtro");

        // 1. Pone la lista en request para que el JSP la recorra.
        if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("servicios", servicioService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("servicios", servicioService.listarTodos());
        }

        // 2. Renderiza la vista protegida dentro de WEB-INF.
        req.getRequestDispatcher("/WEB-INF/views/servicios/servicio_listar.jsp").forward(req, resp);
    }

    // BUSCAR (AUTOCOMPLETE / FILTRO).
    private void buscar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Lee el parámetro filtro.
        String filtro = req.getParameter("filtro");

        // 2. Devuelve una lista filtrada (si filtro vacío, devuelve lista completa).
        List<Servicio> lista;
        if (filtro == null || filtro.isBlank()) {
            lista = servicioService.listarTodos();
        } else {
            lista = servicioService.buscarPorNombreParcial(filtro);
        }

        // 3. Envía la lista al JSP de listado.
        req.setAttribute("servicios", lista);
        req.setAttribute("filtro", (filtro == null) ? "" : filtro);

        // 4. Renderiza el listado.
        req.getRequestDispatcher("/WEB-INF/views/servicios/servicio_listar.jsp").forward(req, resp);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Envía un Servicio vacío a la vista para llenado inicial.
        req.setAttribute("servicio", new Servicio());

        // 2. Renderiza el formulario de servicio.
        req.getRequestDispatcher("/WEB-INF/views/servicios/servicio_form.jsp").forward(req, resp);
    }

    // FORMULARIO EDITAR.
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Convierte el parámetro "id" a Long, si es null se lanza un error.
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 2. Busca el servicio por id usando el service.
        Optional<Servicio> servicio = servicioService.buscarPorId(id);

        // 3. Si no existe, se corta el flujo con error.
        if (servicio.isEmpty()) {
            throw new IllegalArgumentException("No existe un servicio con id: " + id);
        }

        // 4. Coloca el servicio encontrado en request para precargar el formulario.
        req.setAttribute("servicio", servicio.get());

        // 5. Renderiza el mismo JSP de formulario, pero con datos cargados.
        req.getRequestDispatcher("/WEB-INF/views/servicios/servicio_form.jsp").forward(req, resp);
    }

    // ACTIVAR.
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1. Lee el parámetro "id" para activar.
        Long id = parseLong(req.getParameter("id"));

        // 2. Validación de entrada (no se puede activar sin id).
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 3. Cambia el estado del servicio a activo usando la capa service.
        servicioService.activar(id);

        // 4. Redirige al listado tras la operación.
        resp.sendRedirect(req.getContextPath() + "/servicios?action=listar");
    }

    // DESACTIVAR.
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1. Lee el parámetro "id" para desactivar.
        Long id = parseLong(req.getParameter("id"));

        // 2. Validación de entrada (no se puede desactivar sin id).
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 3. Cambia el estado del servicio a inactivo usando la capa service.
        servicioService.desactivar(id);

        // 4. Redirige al listado tras la operación.
        resp.sendRedirect(req.getContextPath() + "/servicios?action=listar");
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES POST
    // ========== ========== ========== 

    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Servicio servicio = new Servicio();

        // Si viene idServicio, se trata de edición; si no, es creación
        Long idServicio = parseLong(req.getParameter("idServicio"));
        servicio.setIdServicio(idServicio);

        servicio.setCodigo(req.getParameter("codigo"));
        servicio.setNombre(req.getParameter("nombre"));
        servicio.setDescripcion(req.getParameter("descripcion"));
        servicio.setPrecioBase(parseBigDecimal(req.getParameter("precioBase")));

        // En creación: activo por defecto true; en edición: tomar el valor del form
        if (idServicio == null) {
            servicio.setActivo(true);
            servicioService.crear(servicio);
        } else {
            servicio.setActivo("true".equals(req.getParameter("activo")));
            servicioService.actualizar(servicio);
        }

        resp.sendRedirect(req.getContextPath() + "/servicios?action=listar");
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES UTILES
    // ========== ========== ========== 

    // PARSEO LONG
    private Long parseLong(String value) {

        // 1. Si no hay valor, se devuelve null para que el llamador valide.
        if (value == null || value.isBlank()) return null;

        try {
            // 2. Convierte el string a Long.
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            // 3. Si el valor no es numérico, se devuelve null para evitar romper el flujo.
            return null;
        }
    }

    // PARSEO BIGDECIMAL
    private BigDecimal parseBigDecimal(String value) {

        // 1. Si no hay valor, se devuelve null para que el service valide.
        if (value == null || value.isBlank()) return null;

        try {
            // 2. Normaliza coma a punto por si el usuario escribe 10,50
            String norm = value.trim().replace(",", ".");
            return new BigDecimal(norm);
        } catch (NumberFormatException e) {
            // 3. Si el valor no es numérico, se devuelve null para que el service lance el error correcto.
            return null;
        }
    }
}