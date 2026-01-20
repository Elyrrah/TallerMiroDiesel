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
import java.util.Optional;
import py.taller.tallermirodiesel.model.Departamento;
import py.taller.tallermirodiesel.service.DepartamentoService;
import py.taller.tallermirodiesel.service.impl.DepartamentoServiceImpl;
import py.taller.tallermirodiesel.service.PaisService;
import py.taller.tallermirodiesel.service.impl.PaisServiceImpl;

/**
 * @author elyrr
 */
@WebServlet("/departamentos")
public class DepartamentoServlet extends HttpServlet {

    private DepartamentoService departamentoService;
    private PaisService paisService;

    @Override
    public void init() {
        this.departamentoService = new DepartamentoServiceImpl();
        this.paisService = new PaisServiceImpl();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String accion = req.getParameter("accion");
        if (accion == null || accion.isBlank()) accion = "listar";

        try {
            switch (accion) {
                case "nuevo" -> mostrarFormularioNuevo(req, resp);
                case "editar" -> mostrarFormularioEditar(req, resp);
                case "activar" -> activar(req, resp);
                case "desactivar" -> desactivar(req, resp);
                case "listar" -> listar(req, resp);
                default -> listar(req, resp);
            }
        } catch (ServletException | IOException e) {
            req.setAttribute("error", e.getMessage());
            listar(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String accion = req.getParameter("accion");
        if (accion == null || accion.isBlank()) accion = "listar";

        try {
            switch (accion) {
                case "crear" -> crear(req, resp);
                case "actualizar" -> actualizar(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/departamentos?accion=listar");
            }
        } catch (IOException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("paises", paisService.listarActivos());

            // Re-render del formulario correspondiente
            if ("actualizar".equals(accion)) {
                mostrarFormularioEditar(req, resp);
            } else {
                mostrarFormularioNuevo(req, resp);
            }
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Si viene idPais, filtramos por país; si no, se lista todo.
        Long idPais = parseLong(req.getParameter("idPais"));
        req.setAttribute("paises", paisService.listarActivos());
        req.setAttribute("idPaisSeleccionado", idPais);

        if (idPais != null) {
            req.setAttribute("departamentos", departamentoService.listarPorPais(idPais));
        } else {
            req.setAttribute("departamentos", departamentoService.listarTodos());
        }

        req.getRequestDispatcher("/WEB-INF/views/departamentos/departamento_listar.jsp").forward(req, resp);
    } 
    
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("departamento", new Departamento());
        req.setAttribute("paises", paisService.listarActivos());

        req.getRequestDispatcher("/WEB-INF/views/departamentos/departamento_form.jsp").forward(req, resp);
    }

    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = parseLong(req.getParameter("id"));
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        Optional<Departamento> departamento = departamentoService.buscarPorId(id);
        if (departamento.isEmpty()) {
            throw new IllegalArgumentException("No existe un departamento con id: " + id);
        }

    req.setAttribute("departamento", departamento.get());
    req.setAttribute("paises", paisService.listarActivos());
    req.getRequestDispatcher("/WEB-INF/views/departamentos/departamento_form.jsp").forward(req, resp);

    }

    private void crear(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Departamento d = new Departamento();
        d.setIdPais(parseLong(req.getParameter("idPais")));
        d.setNombre(req.getParameter("nombre"));
        d.setActivo(true);

        departamentoService.crear(d);
        resp.sendRedirect(req.getContextPath() + "/departamentos?accion=listar");
    }

    private void actualizar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Departamento d = new Departamento();
        d.setIdDepartamento(parseLong(req.getParameter("idDepartamento")));
        d.setIdPais(parseLong(req.getParameter("idPais")));
        d.setNombre(req.getParameter("nombre"));
        d.setActivo("true".equals(req.getParameter("activo"))); // si usas checkbox/select

        departamentoService.actualizar(d);
        resp.sendRedirect(req.getContextPath() + "/departamentos?accion=listar");
    }

    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        departamentoService.activar(id);

        Long idPais = parseLong(req.getParameter("idPais"));
        String url = req.getContextPath() + "/departamentos?accion=listar";
        if (idPais != null) url += "&idPais=" + idPais;

        resp.sendRedirect(url);
    }


    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));
        if (id == null) {
            throw new IllegalArgumentException("Id inválido.");
        }

        departamentoService.desactivar(id);

        Long idPais = parseLong(req.getParameter("idPais"));
        String url = req.getContextPath() + "/departamentos?accion=listar";
        if (idPais != null) url += "&idPais=" + idPais;

        resp.sendRedirect(url);
    }


    private Long parseLong(String value) {
        if (value == null || value.isBlank()) return null;
        return Long.valueOf(value);
    }
}
