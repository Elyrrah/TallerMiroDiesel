package py.taller.tallermirodiesel.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import py.taller.tallermirodiesel.model.Pais;
import py.taller.tallermirodiesel.service.PaisService;
import py.taller.tallermirodiesel.service.impl.PaisServiceImpl;

@WebServlet("/paises")
public class PaisServlet extends HttpServlet {

    private PaisService paisService;

    @Override
    public void init() {
        this.paisService = new PaisServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null || action.isBlank()) action = "list";

        try {
            switch (action) {
                case "new" -> mostrarFormularioNuevo(req, resp);
                case "edit" -> mostrarFormularioEditar(req, resp);
                case "activate" -> activar(req, resp);
                case "deactivate" -> desactivar(req, resp);
                case "list" -> listar(req, resp);
                default -> listar(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            listar(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null || action.isBlank()) action = "list";

        try {
            switch (action) {
                case "create" -> crear(req, resp);
                case "update" -> actualizar(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/paises?action=list");
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());

            // Re-render del formulario correspondiente
            if ("update".equals(action)) {
                mostrarFormularioEditar(req, resp);
            } else {
                mostrarFormularioNuevo(req, resp);
            }
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("paises", paisService.listarTodos());
        req.getRequestDispatcher("/WEB-INF/views/paises/pais_listar.jsp").forward(req, resp);
    }

    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("pais", new Pais());
        req.getRequestDispatcher("/WEB-INF/views/paises/pais_form.jsp").forward(req, resp);
    }

    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Long id = parseLong(req.getParameter("id"));
        Optional<Pais> pais = paisService.buscarPorId(id);
        if (pais.isEmpty()) {
            throw new IllegalArgumentException("No existe un país con id: " + id);
        }
        req.setAttribute("pais", pais.get());
        req.getRequestDispatcher("/WEB-INF/views/paises/pais_form.jsp").forward(req, resp);
    }

    private void crear(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Pais p = new Pais();
        p.setNombre(req.getParameter("nombre"));
        p.setIso2(req.getParameter("iso2"));
        p.setIso3(req.getParameter("iso3"));
        p.setActivo(true);

        paisService.crear(p);
        resp.sendRedirect(req.getContextPath() + "/paises?action=list");
    }

    private void actualizar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Pais p = new Pais();
        p.setIdPais(parseLong(req.getParameter("idPais")));
        p.setNombre(req.getParameter("nombre"));
        p.setIso2(req.getParameter("iso2"));
        p.setIso3(req.getParameter("iso3"));
        p.setActivo("true".equals(req.getParameter("activo"))); // si usas checkbox/select

        paisService.actualizar(p);
        resp.sendRedirect(req.getContextPath() + "/paises?action=list");
    }

    private void activar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));
        paisService.activar(id);
        resp.sendRedirect(req.getContextPath() + "/paises?action=list");
    }

    private void desactivar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long id = parseLong(req.getParameter("id"));
        paisService.desactivar(id);
        resp.sendRedirect(req.getContextPath() + "/paises?action=list");
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) return null;
        return Long.valueOf(value);
    }
}
