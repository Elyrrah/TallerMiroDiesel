/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
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
import py.taller.tallermirodiesel.model.Pais;
import py.taller.tallermirodiesel.service.PaisService;
import py.taller.tallermirodiesel.service.impl.PaisServiceImpl;
/**
 * @author elyrr
 */
@WebServlet("/paises")
// Bloque: Mapeo del servlet (todas las acciones de País entran por /paises).
public class PaisServlet extends HttpServlet {

    // Service usado por el controlador para ejecutar la lógica de negocio de País.
    private PaisService paisService;

    // CICLO DE VIDA DEL SERVLET.
    @Override
    public void init() {
        this.paisService = new PaisServiceImpl();
    }

    
    // ========== ========== ========== ========== ========== 
    // MANEJO DE GET (VISTAS / ACCIONES DE NAVEGACIÓN).
    // ========== ========== ========== ========== ========== 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Lee el parámetro "accion" para decidir qué caso ejecutar.
        String accion = req.getParameter("accion");

        // 2. Si no viene acción, se asume "listar" como comportamiento por defecto.
        if (accion == null || accion.isBlank()) accion = "listar";

        // 4. Router de acciones GET (controlador tipo front-controller por parámetro).
        try {        
            switch (accion) {
                case "nuevo" -> mostrarFormularioNuevo(req, resp);
                case "editar" -> mostrarFormularioEditar(req, resp);
                case "activar" -> activar(req, resp);
                case "desactivar" -> desactivar(req, resp);
                case "buscar" -> buscar(req, resp);
                case "listar" -> listar(req, resp);
                default -> listar(req, resp);
            }
        } catch (ServletException | IOException e) {
            req.setAttribute("error", e.getMessage());
            listar(req, resp);
        }
    }

    
    // ========== ========== ========== ========== ========== 
    // MANEJO DE POST (ACCIONES QUE MODIFICAN DATOS).
    // ========== ========== ========== ========== ========== 
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Lee el parámetro "accion" para decidir qué operación ejecutar.
        String accion = req.getParameter("accion");

        // 2. Si no viene acción, se define un default para evitar nulls.
        if (accion == null || accion.isBlank()) accion = "listar";

        // 3. Router de acciones POST.
        try {
            switch (accion) {
                case "guardar" -> guardar(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/paises?accion=listar");
            }
        } catch (IOException e) {
            
            // 4. Avisa en caso de error.
            req.setAttribute("error", e.getMessage());
            
            // 5. Re-render del formulario correspondiente.
            if ("actualizar".equals(accion)) {
                mostrarFormularioEditar(req, resp);
            } else {
                mostrarFormularioNuevo(req, resp);
            }
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
            req.setAttribute("paises", paisService.buscarPorNombreParcial(filtro));
            req.setAttribute("filtro", filtro);
        } else {
            req.setAttribute("paises", paisService.listarTodos());
        }
        
        // 2. Renderiza la vista protegida dentro de WEB-INF.
        req.getRequestDispatcher("/WEB-INF/views/paises/pais_listar.jsp").forward(req, resp);
    }

    // BUSCAR (AUTOCOMPLETE / FILTRO).
    private void buscar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Lee el parámetro filtro.
        String filtro = req.getParameter("filtro");

        // 2. Devuelve una lista filtrada (si filtro vacío, devuelve lista completa).
        List<Pais> lista;
        if (filtro == null || filtro.isBlank()) {
            lista = paisService.listarTodos();
        } else {
            lista = paisService.buscarPorNombreParcial(filtro);
        }

        // 3. Envía la lista al JSP de listado.
        req.setAttribute("paises", lista);
        req.setAttribute("filtro", (filtro == null) ? "" : filtro);

        // 4. Renderiza el listado.
        req.getRequestDispatcher("/WEB-INF/views/paises/pais_listar.jsp").forward(req, resp);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // 1. Envía un Pais vacío a la vista para llenado inicial.
        req.setAttribute("pais", new Pais());
        
        // 2. Renderiza el formulario de país.
        req.getRequestDispatcher("/WEB-INF/views/paises/pais_form.jsp").forward(req, resp);
    }

    // FORMULARIO EDITAR.
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // 1. Convierte el parámetro "id" a Long, si es null se lanza un error.
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 2. Busca el país por id usando el service.
        Optional<Pais> pais = paisService.buscarPorId(id);

        // 3. Si no existe, se corta el flujo con error.
        if (pais.isEmpty()) {
            throw new IllegalArgumentException("No existe un país con id: " + id);
        }

        // 4. Coloca el país encontrado en request para precargar el formulario.
        req.setAttribute("pais", pais.get());
        
        // 5. Renderiza el mismo JSP de formulario, pero con datos cargados.
        req.getRequestDispatcher("/WEB-INF/views/paises/pais_form.jsp").forward(req, resp);
    }

    // ACTIVAR.
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
        // 1. Lee el parámetro "id" para activar.
        Long id = parseLong(req.getParameter("id"));

        // 2. Validación de entrada (no se puede activar sin id).
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 3. Cambia el estado del país a activo usando la capa service.
        paisService.activar(id);
        
        // 4. Redirige al listado tras la operación.
        resp.sendRedirect(req.getContextPath() + "/paises?accion=listar");
    }


    // DESACTIVAR.
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        
        // 1. Lee el parámetro "id" para desactivar.
        Long id = parseLong(req.getParameter("id"));

        // 2. Validación de entrada (no se puede desactivar sin id).
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 3. Cambia el estado del país a inactivo usando la capa service.
        paisService.desactivar(id);
        
        // 4. Redirige al listado tras la operación.
        resp.sendRedirect(req.getContextPath() + "/paises?accion=listar");
    }


    // ========== ========== ========== 
    // BLOQUE DE ACCIONES POST
    // ========== ========== ========== 
    
    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Pais p = new Pais();

        // Si viene idPais, se trata de edición; si no, es creación
        Long idPais = parseLong(req.getParameter("idPais"));
        p.setIdPais(idPais);

        p.setNombre(req.getParameter("nombre"));
        p.setIso2(req.getParameter("iso2"));
        p.setIso3(req.getParameter("iso3"));

        // En creación: activo por defecto true; en edición: tomar el valor del form
        if (idPais == null) {
            p.setActivo(true);
            paisService.crear(p);
        } else {
            p.setActivo("true".equals(req.getParameter("activo"))); // si usas checkbox/select
            paisService.actualizar(p);
        }

        resp.sendRedirect(req.getContextPath() + "/paises?accion=listar");
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
