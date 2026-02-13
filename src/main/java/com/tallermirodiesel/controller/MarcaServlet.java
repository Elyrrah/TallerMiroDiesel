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
import com.tallermirodiesel.model.Marca;
import com.tallermirodiesel.service.MarcaService;
import com.tallermirodiesel.service.impl.MarcaServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "MarcaServlet", urlPatterns = {"/marcas"})
// Bloque: Mapeo del servlet (todas las acciones de Marca entran por /marcas).
public class MarcaServlet extends HttpServlet {

    // Service usado por el controlador para ejecutar la lógica de negocio de Marca.
    private MarcaService marcaService;

    // CICLO DE VIDA DEL SERVLET.
    @Override
    public void init() {
        this.marcaService = new MarcaServiceImpl();
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

        // 4. Router de acciones GET (controlador tipo front-controller por parámetro).
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
                default -> resp.sendRedirect(req.getContextPath() + "/marcas?action=listar");
            }
        } catch (RuntimeException e) {

            // 4. Avisa en caso de error.
            req.setAttribute("error", e.getMessage());

            // 5. Rehidratar el objeto para no perder los datos ingresados
            Marca marca = new Marca();
            Long idMarca = parseLong(req.getParameter("idMarca"));
            marca.setIdMarca(idMarca);
            marca.setNombre(req.getParameter("nombre"));

            // Nota: En edición tomamos el "activo"; en creación queda true por lógica del servlet/service.
            marca.setActivo("true".equals(req.getParameter("activo")));

            req.setAttribute("marca", marca);

            // 6. Re-render del formulario correspondiente.
            req.getRequestDispatcher("/WEB-INF/views/marcas/marca_form.jsp").forward(req, resp);
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
            req.setAttribute("marcas", marcaService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("marcas", marcaService.listarTodos());
        }

        // 2. Renderiza la vista protegida dentro de WEB-INF.
        req.getRequestDispatcher("/WEB-INF/views/marcas/marca_listar.jsp").forward(req, resp);
    }

    // BUSCAR (AUTOCOMPLETE / FILTRO).
    private void buscar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Lee el parámetro filtro.
        String filtro = req.getParameter("filtro");

        // 2. Devuelve una lista filtrada (si filtro vacío, devuelve lista completa).
        List<Marca> lista;
        if (filtro == null || filtro.isBlank()) {
            lista = marcaService.listarTodos();
        } else {
            lista = marcaService.buscarPorNombreParcial(filtro);
        }

        // 3. Envía la lista al JSP de listado.
        req.setAttribute("marcas", lista);
        req.setAttribute("filtro", (filtro == null) ? "" : filtro);

        // 4. Renderiza el listado.
        req.getRequestDispatcher("/WEB-INF/views/marcas/marca_listar.jsp").forward(req, resp);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Envía un Marca vacío a la vista para llenado inicial.
        req.setAttribute("marca", new Marca());

        // 2. Renderiza el formulario de marca.
        req.getRequestDispatcher("/WEB-INF/views/marcas/marca_form.jsp").forward(req, resp);
    }

    // FORMULARIO EDITAR.
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Convierte el parámetro "id" a Long, si es null se lanza un error.
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 2. Busca la marca por id usando el service.
        Optional<Marca> marca = marcaService.buscarPorId(id);

        // 3. Si no existe, se corta el flujo con error.
        if (marca.isEmpty()) {
            throw new IllegalArgumentException("No existe una marca con id: " + id);
        }

        // 4. Coloca la marca encontrada en request para precargar el formulario.
        req.setAttribute("marca", marca.get());

        // 5. Renderiza el mismo JSP de formulario, pero con datos cargados.
        req.getRequestDispatcher("/WEB-INF/views/marcas/marca_form.jsp").forward(req, resp);
    }

    // ACTIVAR.
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1. Lee el parámetro "id" para activar.
        Long id = parseLong(req.getParameter("id"));

        // 2. Validación de entrada (no se puede activar sin id).
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 3. Cambia el estado de la marca a activo usando la capa service.
        marcaService.activar(id);

        // 4. Redirige al listado tras la operación.
        resp.sendRedirect(req.getContextPath() + "/marcas?action=listar");
    }

    // DESACTIVAR.
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1. Lee el parámetro "id" para desactivar.
        Long id = parseLong(req.getParameter("id"));

        // 2. Validación de entrada (no se puede desactivar sin id).
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 3. Cambia el estado de la marca a inactivo usando la capa service.
        marcaService.desactivar(id);

        // 4. Redirige al listado tras la operación.
        resp.sendRedirect(req.getContextPath() + "/marcas?action=listar");
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES POST
    // ========== ========== ========== 

    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Marca marca = new Marca();

        // Si viene idMarca, se trata de edición; si no, es creación
        Long idMarca = parseLong(req.getParameter("idMarca"));
        marca.setIdMarca(idMarca);

        marca.setNombre(req.getParameter("nombre"));

        // En creación: activo por defecto true; en edición: tomar el valor del form
        if (idMarca == null) {
            marca.setActivo(true);
            marcaService.crear(marca);
        } else {
            marca.setActivo("true".equals(req.getParameter("activo")));
            marcaService.actualizar(marca);
        }

        resp.sendRedirect(req.getContextPath() + "/marcas?action=listar");
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES UTILES
    // ========== ========== ========== 

    // PARSEO
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
}