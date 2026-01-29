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
import py.taller.tallermirodiesel.model.Departamento;
import py.taller.tallermirodiesel.service.DepartamentoService;
import py.taller.tallermirodiesel.service.impl.DepartamentoServiceImpl;
import py.taller.tallermirodiesel.service.PaisService;
import py.taller.tallermirodiesel.service.impl.PaisServiceImpl;
/**
 * @author elyrr
 */
@WebServlet(name = "DepartamentoServlet", urlPatterns = {"/departamentos"})
// Bloque: Mapeo del servlet (todas las acciones de Departamento entran por /departamentos).
public class DepartamentoServlet extends HttpServlet {

    // Service usado por el controlador para ejecutar la lógica de negocio de Departamento.
    private DepartamentoService departamentoService;

    // Service usado para poblar combos/listas de Países.
    private PaisService paisService;

    // CICLO DE VIDA DEL SERVLET.
    @Override
    public void init() {
        this.departamentoService = new DepartamentoServiceImpl();
        this.paisService = new PaisServiceImpl();
    }

    // ========== ========== ========== ========== ========== 
    // MANEJO DE GET (VISTAS / ACCIONES DE NAVEGACIÓN).
    // ========== ========== ========== ========== ========== 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Lee el parámetro "accion" para decidir qué caso ejecutar.
        // AJUSTE: ahora usamos "action" (igual que PaisServlet).
        String accion = req.getParameter("action");

        // 2. Si no viene acción, se asume "listar" como comportamiento por defecto.
        // AJUSTE: ahora el default es "list" (igual que PaisServlet).
        if (accion == null || accion.isBlank()) accion = "list";

        // 4. Router de acciones GET (controlador tipo front-controller por parámetro).
        try {
            switch (accion) {
                case "new" -> mostrarFormularioNuevo(req, resp);          // antes: "nuevo"
                case "edit" -> mostrarFormularioEditar(req, resp);        // antes: "editar"
                case "activate" -> activar(req, resp);                    // antes: "activar"
                case "deactivate" -> desactivar(req, resp);               // antes: "desactivar"
                case "search" -> buscar(req, resp);                       // antes: "buscar"
                case "list" -> listar(req, resp);                         // antes: "listar"
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

        // 1. Lee el parámetro "accion" para decidir qué operación ejecutar.
        // AJUSTE: ahora usamos "action" también en POST.
        String accion = req.getParameter("action");

        // 2. Si no viene acción, se asume "guardar" como comportamiento por defecto.
        // AJUSTE: ahora el default es "save" (igual que PaisServlet).
        if (accion == null || accion.isBlank()) accion = "save";

        // 3. Router de acciones POST.
        try {
            switch (accion) {
                case "save" -> guardar(req, resp); // antes: "guardar"
                default -> resp.sendRedirect(req.getContextPath() + "/departamentos?action=list");
            }
        } catch (RuntimeException e) {

            // 4. Avisa en caso de error.
            req.setAttribute("error", e.getMessage());

            // 5. Repone datos auxiliares necesarios para re-render del formulario (combo de países).
            req.setAttribute("paises", paisService.listarActivos());

            // 5.1 AJUSTE: rehidratar objeto Departamento para no perder lo que el usuario escribió.
            Departamento d = new Departamento();
            Long idDepartamento = parseLong(req.getParameter("idDepartamento"));
            d.setIdDepartamento(idDepartamento);
            d.setIdPais(parseLong(req.getParameter("idPais")));
            d.setNombre(req.getParameter("nombre"));
            d.setActivo("true".equals(req.getParameter("activo")));

            req.setAttribute("departamento", d);

            // 6. Re-render del formulario correspondiente.
            req.getRequestDispatcher("/WEB-INF/views/departamentos/departamento_form.jsp").forward(req, resp);
        }
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES GET
    // ========== ========== ========== 

    // LISTADO.
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Si viene idPais, filtramos por país; si no, se lista todo.
        Long idPais = parseLong(req.getParameter("idPais"));
        
        // 2. Lee el filtro opcional (para búsquedas automáticas).
        String filtro = req.getParameter("filtro");

        // 3. Carga países activos para el combo/filtro.
        req.setAttribute("paises", paisService.listarActivos());

        // 4. Guarda el país seleccionado para mantener el filtro en la vista.
        req.setAttribute("idPaisSeleccionado", idPais);

        // 5. Guarda el filtro para mantener el texto en la vista.
        req.setAttribute("filtro", (filtro == null) ? "" : filtro);

        // 6. Decide la fuente de datos según exista o no el filtro por país y/o texto.
        if (idPais != null && filtro != null && !filtro.isBlank()) {
            // Si hay idPais + filtro: se filtra por país y luego por nombre (en memoria).
            List<Departamento> base = departamentoService.listarPorPais(idPais);
            String filtroUpper = filtro.trim().toUpperCase();
            List<Departamento> filtrados = base.stream().filter(d -> d.getNombre() != null 
                    && d.getNombre().toUpperCase().contains(filtroUpper)).toList();
            req.setAttribute("departamentos", filtrados);

        } else if (idPais != null) {
            req.setAttribute("departamentos", departamentoService.listarPorPais(idPais));

        } else if (filtro != null && !filtro.isBlank()) {
            req.setAttribute("departamentos", departamentoService.buscarPorNombreParcial(filtro));

        } else {
            req.setAttribute("departamentos", departamentoService.listarTodos());
        }

        // 7. Renderiza la vista de listado.
        req.getRequestDispatcher("/WEB-INF/views/departamentos/departamento_listar.jsp").forward(req, resp);
    }

    // BUSCAR (AUTOCOMPLETE / FILTRO).
    private void buscar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Lee filtros.
        Long idPais = parseLong(req.getParameter("idPais"));
        String filtro = req.getParameter("filtro");

        // 2. Carga países activos para el combo/filtro.
        req.setAttribute("paises", paisService.listarActivos());

        // 3. Guarda el país seleccionado para mantener el filtro en la vista.
        req.setAttribute("idPaisSeleccionado", idPais);

        // 4. Guarda el filtro para mantener el texto en la vista.
        req.setAttribute("filtro", (filtro == null) ? "" : filtro);

        // 5. Aplica búsqueda según filtros.
        List<Departamento> lista;
        if (idPais != null && filtro != null && !filtro.isBlank()) {
            List<Departamento> base = departamentoService.listarPorPais(idPais);
            String filtroUpper = filtro.trim().toUpperCase();
            lista = base.stream().filter(d -> d.getNombre() != null && 
                    d.getNombre().toUpperCase().contains(filtroUpper)).toList();
        } else if (idPais != null) {
            lista = departamentoService.listarPorPais(idPais);
        } else if (filtro != null && !filtro.isBlank()) {
            lista = departamentoService.buscarPorNombreParcial(filtro);
        } else {
            lista = departamentoService.listarTodos();
        }

        // 6. Envía la lista al JSP.
        req.setAttribute("departamentos", lista);

        // 7. Renderiza la vista de listado.
        req.getRequestDispatcher("/WEB-INF/views/departamentos/departamento_listar.jsp").forward(req, resp);
    }

    // FORMULARIO NUEVO.
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Envía un Departamento vacío a la vista para llenado inicial.
        req.setAttribute("departamento", new Departamento());

        // 2. Carga países activos para poblar el combo de selección.
        req.setAttribute("paises", paisService.listarActivos());

        // 3. Renderiza el formulario de departamento.
        req.getRequestDispatcher("/WEB-INF/views/departamentos/departamento_form.jsp").forward(req, resp);
    }

    // FORMULARIO EDITAR.
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Convierte el parámetro "id" a Long, si es null se lanza un error.
        Long id = parseLong(req.getParameter("id"));

        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 2. Busca el departamento por id usando el service.
        Optional<Departamento> departamento = departamentoService.buscarPorId(id);

        // 3. Si no existe, se corta el flujo con error.
        if (departamento.isEmpty()) {
            throw new IllegalArgumentException("No existe un departamento con id: " + id);
        }

        // 4. Coloca el departamento encontrado en request para precargar el formulario.
        req.setAttribute("departamento", departamento.get());

        // 5. Carga países activos para poblar el combo de selección.
        req.setAttribute("paises", paisService.listarActivos());

        // 6. Renderiza el formulario con datos cargados.
        req.getRequestDispatcher("/WEB-INF/views/departamentos/departamento_form.jsp").forward(req, resp);
    }

    // ACTIVAR.
    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1. Lee el parámetro "id" para activar.
        Long id = parseLong(req.getParameter("id"));

        // 2. Validación de entrada (no se puede activar sin id).
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 3. Cambia el estado del departamento a activo usando la capa service.
        departamentoService.activar(id);

        // 4. Reconstruye la URL preservando el filtro por país si venía en la petición.
        Long idPais = parseLong(req.getParameter("idPais"));
        // AJUSTE: action=list
        String url = req.getContextPath() + "/departamentos?action=list";
        if (idPais != null) url += "&idPais=" + idPais;

        // 5. Preserva filtro de texto si venía en la petición.
        String filtro = req.getParameter("filtro");
        if (filtro != null && !filtro.isBlank()) url += "&filtro=" + filtro;

        // 6. Redirige al listado tras la operación.
        resp.sendRedirect(url);
    }

    // DESACTIVAR.
    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1. Lee el parámetro "id" para desactivar.
        Long id = parseLong(req.getParameter("id"));

        // 2. Validación de entrada (no se puede desactivar sin id).
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        // 3. Cambia el estado del departamento a inactivo usando la capa service.
        departamentoService.desactivar(id);

        // 4. Reconstruye la URL preservando el filtro por país si venía en la petición.
        Long idPais = parseLong(req.getParameter("idPais"));
        // AJUSTE: action=list
        String url = req.getContextPath() + "/departamentos?action=list";
        if (idPais != null) url += "&idPais=" + idPais;

        // 5. Preserva filtro de texto si venía en la petición.
        String filtro = req.getParameter("filtro");
        if (filtro != null && !filtro.isBlank()) url += "&filtro=" + filtro;

        // 6. Redirige al listado tras la operación.
        resp.sendRedirect(url);
    }

    // ========== ========== ========== 
    // BLOQUE DE ACCIONES POST
    // ========== ========== ========== 

    // GUARDAR (CREAR O ACTUALIZAR).
    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1. Lee el idDepartamento si viene (si no viene, es creación).
        Long idDepartamento = parseLong(req.getParameter("idDepartamento"));

        // 2. Construye el objeto Departamento a partir de los parámetros del formulario.
        Departamento d = new Departamento();
        d.setIdDepartamento(idDepartamento);
        d.setIdPais(parseLong(req.getParameter("idPais")));
        d.setNombre(req.getParameter("nombre"));

        // 3. Si no tiene id, se crea; si tiene id, se actualiza.
        if (idDepartamento == null) {
            d.setActivo(true);
            departamentoService.crear(d);
        } else {
            d.setActivo("true".equals(req.getParameter("activo"))); // si usas checkbox/select
            departamentoService.actualizar(d);
        }

        // 4. Redirige al listado (si venía con filtro por país, lo preserva).
        Long idPaisFiltro = parseLong(req.getParameter("idPaisFiltro"));
        // AJUSTE: action=list
        String url = req.getContextPath() + "/departamentos?action=list";
        if (idPaisFiltro != null) url += "&idPais=" + idPaisFiltro;

        resp.sendRedirect(url);
    }

    // PARSEO.
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
