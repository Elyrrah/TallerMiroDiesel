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
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import py.taller.tallermirodiesel.model.Ciudad;
import py.taller.tallermirodiesel.model.Distrito;
import py.taller.tallermirodiesel.service.CiudadService;
import py.taller.tallermirodiesel.service.DistritoService;
import py.taller.tallermirodiesel.service.impl.CiudadServiceImpl;
import py.taller.tallermirodiesel.service.impl.DistritoServiceImpl;

/**
 * @author elyrr
 */
@WebServlet(name = "DistritoServlet", urlPatterns = {"/distritos"})
public class DistritoServlet extends HttpServlet {

    // Service utilizado por el Servlet para acceder a la capa de negocio.
    private final DistritoService distritoService = new DistritoServiceImpl();

    // Service para cargar combo/filtro de ciudades
    private final CiudadService ciudadService = new CiudadServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Accion por defecto
        String accion = request.getParameter("accion");
        if (accion == null || accion.isBlank()) {
            accion = "listar";
        }

        try {
            switch (accion) {
                case "nuevo" -> mostrarFormularioNuevo(request, response);
                case "editar" -> mostrarFormularioEditar(request, response);
                case "activar" -> activar(request, response);
                case "desactivar" -> desactivar(request, response);
                case "listar" -> listar(request, response);
                default -> listar(request, response);
            }
        } catch (ServletException | IOException e) {
            // Manejo simple de errores: setear mensaje y volver al listado
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
        } catch (IOException e) {
            // Si falla guardar, devolvemos al formulario con el error y los valores cargados
            request.setAttribute("error", e.getMessage());
            reenviarFormularioConDatos(request, response);
        }
    }

    //  Carga lista de ciudades para filtro y formulario
    private void cargarCiudades(HttpServletRequest request) {
        // Preferible listarActivos para combos; si no lo tenés, usá listarTodos()
        List<Ciudad> ciudades = ciudadService.listarActivos();
        request.setAttribute("ciudades", ciudades);
    }

    //  Lista todos los Distritos (con filtro por ciudad y búsqueda por nombre)
    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Necesario para que el filtro traiga ciudades
        cargarCiudades(request);

        Long idCiudad = parseLongNullable(request.getParameter("idCiudad"));
        String q = request.getParameter("q");

        // Guardamos los valores para que el JSP los mantenga seleccionados
        request.setAttribute("idCiudad", idCiudad);
        request.setAttribute("q", q);

        // Base: lista completa o lista filtrada por ciudad
        List<Distrito> lista = (idCiudad == null)
                ? distritoService.listarTodos()
                : distritoService.listarPorCiudad(idCiudad);

        // Búsqueda por nombre (opcional)
        if (q != null) {
            String filtro = q.trim();
            if (!filtro.isBlank()) {
                String needle = filtro.toUpperCase(Locale.ROOT);

                lista = lista.stream()
                        .filter(d -> d.getNombre() != null && d.getNombre().toUpperCase(Locale.ROOT).contains(needle))
                        .collect(Collectors.toList());
            }
        }

        request.setAttribute("listaDistritos", lista);

        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_listar.jsp").forward(request, response);
    }

    //  Muestra el formulario para crear un nuevo Distrito
    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Necesario para el combo de ciudades en el form
        cargarCiudades(request);

        request.setAttribute("distrito", new Distrito());
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_form.jsp").forward(request, response);
    }

    //  Muestra el formulario para editar un Distrito existente
    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Necesario para el combo de ciudades en el form
        cargarCiudades(request);

        Long id = parseLong(request.getParameter("id"));

        Optional<Distrito> opt = distritoService.buscarPorId(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("No existe un distrito con id: " + id);
        }

        request.setAttribute("distrito", opt.get());
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_form.jsp").forward(request, response);
    }

    //  Guarda (crea o actualiza) un Distrito
    private void guardar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));
        Long idCiudad = parseLong(request.getParameter("idCiudad"));
        String nombre = request.getParameter("nombre");
        boolean activo = parseBooleanDefaultTrue(request.getParameter("activo"));

        Distrito d = new Distrito();
        d.setIdDistrito(idDistrito);
        d.setIdCiudad(idCiudad);
        d.setNombre(nombre);
        d.setActivo(activo);

        if (idDistrito == null) {
            distritoService.crear(d);
        } else {
            distritoService.actualizar(d);
        }

        response.sendRedirect(request.getContextPath() + "/distritos?accion=listar");
    }

    //  Activa un Distrito
    private void activar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = parseLong(request.getParameter("id"));
        distritoService.activar(id);

        response.sendRedirect(request.getContextPath() + "/distritos?accion=listar");
    }

    //  Desactiva un Distrito
    private void desactivar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = parseLong(request.getParameter("id"));
        distritoService.desactivar(id);

        response.sendRedirect(request.getContextPath() + "/distritos?accion=listar");
    }

    //  Reenvía al formulario usando los parámetros del request para no perder lo que el usuario escribió
    private void reenviarFormularioConDatos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Necesario para el combo de ciudades en el form cuando hay error
        cargarCiudades(request);

        Long idDistrito = parseLongNullable(request.getParameter("idDistrito"));
        Long idCiudad = parseLongNullable(request.getParameter("idCiudad"));
        String nombre = request.getParameter("nombre");
        boolean activo = parseBooleanDefaultTrue(request.getParameter("activo"));

        Distrito d = new Distrito();
        d.setIdDistrito(idDistrito);
        d.setIdCiudad(idCiudad);
        d.setNombre(nombre);
        d.setActivo(activo);

        request.setAttribute("distrito", d);
        request.getRequestDispatcher("/WEB-INF/views/distritos/distrito_form.jsp").forward(request, response);
    }

    //  Helpers de parseo
    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Parámetro numérico obligatorio.");
        }
        try {
            Long n = Long.valueOf(value);
            if (n <= 0) {
                throw new IllegalArgumentException("El parámetro numérico debe ser mayor a 0.");
            }
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Parámetro numérico inválido: " + value);
        }
    }

    private Long parseLongNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Long n = Long.valueOf(value);
            if (n <= 0) {
                return null;
            }
            return n;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean parseBooleanDefaultTrue(String value) {
        // Si el checkbox no viene, por defecto true (ajustalo si preferís false)
        if (value == null) {
            return true;
        }
        return value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("on")
                || value.equalsIgnoreCase("1")
                || value.equalsIgnoreCase("yes");
    }
}
