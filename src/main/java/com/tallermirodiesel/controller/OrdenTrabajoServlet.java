/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tallermirodiesel.controller;

import com.tallermirodiesel.dao.OrdenTrabajoListadoDAO;
import com.tallermirodiesel.dao.impl.OrdenTrabajoListadoDAOImpl;
import com.tallermirodiesel.dto.OrdenTrabajoVerDTO;
import com.tallermirodiesel.model.OrdenTrabajo;
import com.tallermirodiesel.model.OrdenTrabajoDetalle;
import com.tallermirodiesel.model.SesionDeUsuario;
import com.tallermirodiesel.model.enums.EstadoOrdenTrabajoEnum;
import com.tallermirodiesel.model.enums.FuenteReferenciaClienteEnum;
import com.tallermirodiesel.model.enums.TipoIngresoOrdenEnum;
import com.tallermirodiesel.service.OrdenTrabajoDetalleService;
import com.tallermirodiesel.service.OrdenTrabajoService;
import com.tallermirodiesel.dao.MarcaDAO;
import com.tallermirodiesel.dao.ModeloDAO;
import com.tallermirodiesel.dao.impl.MarcaDAOImpl;
import com.tallermirodiesel.dao.impl.ModeloDAOImpl;
import com.tallermirodiesel.dto.ClienteEmpresaListadoDTO;
import com.tallermirodiesel.dto.ClientePersonaListadoDTO;
import com.tallermirodiesel.model.Cliente;
import com.tallermirodiesel.model.ClienteEmpresa;
import com.tallermirodiesel.model.ClientePersona;
import com.tallermirodiesel.model.Componente;
import com.tallermirodiesel.model.Vehiculo;
import com.tallermirodiesel.model.enums.TipoVehiculoEnum;
import com.tallermirodiesel.service.ClienteEmpresaService;
import com.tallermirodiesel.service.ClienteListadoService;
import com.tallermirodiesel.service.ClientePersonaService;
import com.tallermirodiesel.service.ClienteService;
import com.tallermirodiesel.service.ComponenteService;
import com.tallermirodiesel.service.DistritoService;
import com.tallermirodiesel.service.LocalidadService;
import com.tallermirodiesel.service.ServicioService;
import com.tallermirodiesel.service.TipoComponenteService;
import com.tallermirodiesel.service.VehiculoService;
import com.tallermirodiesel.service.impl.ClienteEmpresaServiceImpl;
import com.tallermirodiesel.service.impl.ClienteListadoServiceImpl;
import com.tallermirodiesel.service.impl.ClientePersonaServiceImpl;
import com.tallermirodiesel.service.impl.ClienteServiceImpl;
import com.tallermirodiesel.service.impl.ComponenteServiceImpl;
import com.tallermirodiesel.service.impl.DistritoServiceImpl;
import com.tallermirodiesel.service.impl.LocalidadServiceImpl;
import com.tallermirodiesel.service.impl.TipoComponenteServiceImpl;
import com.tallermirodiesel.service.impl.VehiculoServiceImpl;
import java.io.PrintWriter;
import java.util.List;
import com.tallermirodiesel.service.impl.OrdenTrabajoDetalleServiceImpl;
import com.tallermirodiesel.service.impl.OrdenTrabajoServiceImpl;
import com.tallermirodiesel.service.impl.ServicioServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * @author elyrr
 * Servlet que gestiona las Órdenes de Trabajo.
 * URL base: /ordenes-trabajo
 */
@WebServlet(name = "OrdenTrabajoServlet", urlPatterns = {"/ordenes-trabajo"})
public class OrdenTrabajoServlet extends HttpServlet {

    // Inicializa todos los servicios y DAOs necesarios para la gestión de órdenes de trabajo
    private final OrdenTrabajoService ordenTrabajoService               = new OrdenTrabajoServiceImpl();
    private final OrdenTrabajoDetalleService ordenTrabajoDetalleService = new OrdenTrabajoDetalleServiceImpl();
    private final OrdenTrabajoListadoDAO ordenTrabajoListadoDAO         = new OrdenTrabajoListadoDAOImpl();
    private final ServicioService servicioService                       = new ServicioServiceImpl();
    private final ClienteListadoService clienteListadoService           = new ClienteListadoServiceImpl();
    private final VehiculoService vehiculoService                      = new VehiculoServiceImpl();
    private final ComponenteService componenteService                  = new ComponenteServiceImpl();
    private final ClienteService clienteService                        = new ClienteServiceImpl();
    private final ClientePersonaService clientePersonaService          = new ClientePersonaServiceImpl();
    private final ClienteEmpresaService clienteEmpresaService          = new ClienteEmpresaServiceImpl();
    private final DistritoService distritoService                      = new DistritoServiceImpl();
    private final LocalidadService localidadService                    = new LocalidadServiceImpl();
    private final TipoComponenteService tipoComponenteService          = new TipoComponenteServiceImpl();
    private final MarcaDAO marcaDAO                                    = new MarcaDAOImpl();
    private final ModeloDAO modeloDAO                                  = new ModeloDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Obtiene el parámetro 'action' de la URL
        String action = req.getParameter("action");

        // Si no hay acción, por defecto es 'listar'
        if (action == null || action.isBlank()) {
            action = "listar";
        }

        try {
            // Switch para manejar las diferentes acciones GET
            switch (action) {
                case "listar"        -> listar(req, resp);
                case "buscar"        -> listar(req, resp);
                case "nuevo"         -> mostrarFormularioNuevo(req, resp);
                case "editar"        -> mostrarFormularioEditar(req, resp);
                case "ver"           -> ver(req, resp);
                case "activar"       -> activar(req, resp);
                case "desactivar"    -> desactivar(req, resp);
                case "eliminar"      -> eliminar(req, resp);
                case "nuevoDetalle"  -> mostrarFormularioNuevoDetalle(req, resp);
                case "editarDetalle" -> mostrarFormularioEditarDetalle(req, resp);
                case "quitarDetalle"  -> quitarDetalle(req, resp);
                case "ajaxClientes"   -> ajaxClientes(req, resp);
                case "ajaxVehiculos"  -> ajaxVehiculos(req, resp);
                case "ajaxComponentes"-> ajaxComponentes(req, resp);
                default               -> listar(req, resp);
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            listar(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Obtiene el parámetro 'action' de la petición POST
        String action = req.getParameter("action");

        // Si no hay acción, por defecto es 'guardar'
        if (action == null || action.isBlank()) {
            action = "guardar";
        }

        try {
            // Switch para manejar las diferentes acciones POST
            switch (action) {
                case "guardar"              -> guardar(req, resp);
                case "guardarDetalle"       -> guardarDetalle(req, resp);
                case "ajaxGuardarCliente"   -> ajaxGuardarCliente(req, resp);
                case "ajaxGuardarVehiculo"  -> ajaxGuardarVehiculo(req, resp);
                case "ajaxGuardarComponente"-> ajaxGuardarComponente(req, resp);
                default -> resp.sendRedirect(req.getContextPath() + "/ordenes-trabajo?action=listar");
            }
        } catch (RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            // Si el error viene del formulario de detalle, volvemos a ese formulario
            if ("guardarDetalle".equals(action)) {
                reenviarFormularioDetalle(req, resp);
            } else {
                reenviarFormularioCabecera(req, resp);
            }
        }
    }

    // Lista las OTs aplicando filtros opcionales de estado o día
    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String estadoParam = req.getParameter("estado");
        String diaParam    = req.getParameter("dia");

        if (estadoParam != null && !estadoParam.isBlank()) {
            // Filtra por estado
            EstadoOrdenTrabajoEnum estado = EstadoOrdenTrabajoEnum.valueOf(estadoParam);
            req.setAttribute("ordenes", ordenTrabajoListadoDAO.listarPorEstado(estado));
            req.setAttribute("estadoFiltro", estadoParam);
        } else if (diaParam != null && !diaParam.isBlank()) {
            // Filtra por día de ingreso
            LocalDate dia = LocalDate.parse(diaParam);
            req.setAttribute("ordenes", ordenTrabajoListadoDAO.listarPorDia(dia));
            req.setAttribute("diaFiltro", diaParam);
        } else {
            // Sin filtros muestra las activas
            req.setAttribute("ordenes", ordenTrabajoListadoDAO.listarActivos());
        }

        req.setAttribute("estados", EstadoOrdenTrabajoEnum.values());
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/ordenes/orden_trabajo_listar.jsp").forward(req, resp);
    }

    // Muestra la pantalla de detalle completo de una OT con sus servicios cargados
    private void ver(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long id = parseLong(req.getParameter("id"));

        OrdenTrabajoVerDTO ot = ordenTrabajoListadoDAO.buscarVerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una OT con id: " + id));

        req.setAttribute("ot", ot);
        req.setAttribute("detalles", ordenTrabajoDetalleService.listarPorOrden(id));
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/ordenes/orden_trabajo_ver.jsp").forward(req, resp);
    }

    // Muestra el formulario vacío para registrar una nueva OT
    private void mostrarFormularioNuevo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("ot", new OrdenTrabajo());
        req.setAttribute("tiposIngreso", TipoIngresoOrdenEnum.values());
        req.setAttribute("fuentesReferencia", FuenteReferenciaClienteEnum.values());
        // Carga los clientes activos para el datalist del buscador
        req.setAttribute("clientesPersona", clienteListadoService.listarPersonas(null, true));
        req.setAttribute("clientesEmpresa", clienteListadoService.listarEmpresas(null, true));
        // Carga los vehículos y componentes activos para sus datalists
        req.setAttribute("vehiculos", vehiculoService.listarActivos());
        req.setAttribute("componentes", componenteService.listarActivos());
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/ordenes/orden_trabajo_form.jsp").forward(req, resp);
    }

    // Muestra el formulario precargado con los datos de una OT existente para editarla
    private void mostrarFormularioEditar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long id = parseLong(req.getParameter("id"));

        // Carga la OT existente para precargar el formulario
        OrdenTrabajo ot = ordenTrabajoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe una OT con id: " + id));

        req.setAttribute("ot", ot);
        req.setAttribute("tiposIngreso", TipoIngresoOrdenEnum.values());
        req.setAttribute("fuentesReferencia", FuenteReferenciaClienteEnum.values());
        // Carga los clientes activos para el datalist del buscador
        req.setAttribute("clientesPersona", clienteListadoService.listarPersonas(null, true));
        req.setAttribute("clientesEmpresa", clienteListadoService.listarEmpresas(null, true));
        // Carga los vehículos y componentes activos para sus datalists
        req.setAttribute("vehiculos", vehiculoService.listarActivos());
        req.setAttribute("componentes", componenteService.listarActivos());
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/ordenes/orden_trabajo_form.jsp").forward(req, resp);
    }

    // Guarda la cabecera de una OT, creando o actualizando según si tiene ID o no
    private void guardar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        OrdenTrabajo ot = construirCabeceraDesdeRequest(req);

        if (ot.getIdOrdenTrabajo() == null) {
            // Lee el usuario logueado desde la sesión
            HttpSession sesion = req.getSession(false);
            SesionDeUsuario usuarioSesion = (SesionDeUsuario) sesion.getAttribute("usuarioSesion");
            ot.setIdUsuario(usuarioSesion.getIdUsuario());

            // Después de crear, redirige a la pantalla de detalle de esa OT
            Long idNuevo = ordenTrabajoService.crear(ot);
            resp.sendRedirect(req.getContextPath() + "/ordenes-trabajo?action=ver&id=" + idNuevo);
        } else {
            ordenTrabajoService.actualizar(ot);
            resp.sendRedirect(req.getContextPath() + "/ordenes-trabajo?action=ver&id=" + ot.getIdOrdenTrabajo());
        }
    }

    // Reactiva una OT que había sido desactivada
    private void activar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long id = parseLong(req.getParameter("id"));
        ordenTrabajoService.activar(id);
        resp.sendRedirect(construirUrlRetornoListado(req));
    }

    // Desactiva una OT sin eliminarla físicamente
    private void desactivar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long id = parseLong(req.getParameter("id"));
        ordenTrabajoService.desactivar(id);
        resp.sendRedirect(construirUrlRetornoListado(req));
    }

    // Elimina físicamente una OT de la base de datos
    private void eliminar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long id = parseLong(req.getParameter("id"));
        ordenTrabajoService.eliminar(id);
        resp.sendRedirect(req.getContextPath() + "/ordenes-trabajo?action=listar");
    }

    // Muestra el formulario vacío para agregar un nuevo servicio a una OT
    private void mostrarFormularioNuevoDetalle(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long idOrden = parseLong(req.getParameter("idOrden"));

        // Verifica que la OT existe antes de mostrar el formulario
        ordenTrabajoService.buscarPorId(idOrden)
                .orElseThrow(() -> new IllegalArgumentException("No existe una OT con id: " + idOrden));

        OrdenTrabajoDetalle detalle = new OrdenTrabajoDetalle();
        detalle.setIdOrdenTrabajo(idOrden);

        req.setAttribute("detalle", detalle);
        req.setAttribute("idOrden", idOrden);
        req.setAttribute("servicios", servicioService.listarActivos());
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/ordenes/orden_trabajo_detalle_form.jsp").forward(req, resp);
    }

    // Muestra el formulario precargado con los datos de un servicio existente para editarlo
    private void mostrarFormularioEditarDetalle(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Long idDetalle = parseLong(req.getParameter("idDetalle"));

        // Carga el detalle existente para precargar el formulario
        OrdenTrabajoDetalle detalle = ordenTrabajoDetalleService.buscarPorId(idDetalle)
                .orElseThrow(() -> new IllegalArgumentException("No existe un detalle con id: " + idDetalle));

        req.setAttribute("detalle", detalle);
        req.setAttribute("idOrden", detalle.getIdOrdenTrabajo());
        req.setAttribute("servicios", servicioService.listarActivos());
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/ordenes/orden_trabajo_detalle_form.jsp").forward(req, resp);
    }

    // Guarda un servicio de la OT, creando o actualizando según si tiene ID o no
    private void guardarDetalle(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        OrdenTrabajoDetalle detalle = construirDetalleDesdeRequest(req);

        if (detalle.getIdDetalle() == null) {
            ordenTrabajoDetalleService.crear(detalle);
        } else {
            ordenTrabajoDetalleService.actualizar(detalle);
        }

        // Vuelve a la pantalla de la OT
        resp.sendRedirect(req.getContextPath() + "/ordenes-trabajo?action=ver&id=" + detalle.getIdOrdenTrabajo());
    }

    // Desactiva un servicio de la OT sin eliminarlo físicamente
    private void quitarDetalle(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        Long idDetalle = parseLong(req.getParameter("idDetalle"));
        Long idOrden   = parseLong(req.getParameter("idOrden"));

        ordenTrabajoDetalleService.desactivar(idDetalle);
        resp.sendRedirect(req.getContextPath() + "/ordenes-trabajo?action=ver&id=" + idOrden);
    }

    // Reenvía el formulario de cabecera con los datos del request en caso de error
    private void reenviarFormularioCabecera(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("ot", construirCabeceraDesdeRequest(req));
        req.setAttribute("tiposIngreso", TipoIngresoOrdenEnum.values());
        req.setAttribute("fuentesReferencia", FuenteReferenciaClienteEnum.values());
        // Carga los clientes activos para el datalist del buscador
        req.setAttribute("clientesPersona", clienteListadoService.listarPersonas(null, true));
        req.setAttribute("clientesEmpresa", clienteListadoService.listarEmpresas(null, true));
        // Carga los vehículos y componentes activos para sus datalists
        req.setAttribute("vehiculos", vehiculoService.listarActivos());
        req.setAttribute("componentes", componenteService.listarActivos());
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/ordenes/orden_trabajo_form.jsp").forward(req, resp);
    }

    // Reenvía el formulario de detalle con los datos del request en caso de error
    private void reenviarFormularioDetalle(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("detalle", construirDetalleDesdeRequest(req));
        req.setAttribute("idOrden", parseLongNullable(req.getParameter("idOrdenTrabajo")));
        req.setAttribute("servicios", servicioService.listarActivos());
        req.getRequestDispatcher("/WEB-INF/views/ordenes_de_trabajo/ordenes/orden_trabajo_detalle_form.jsp").forward(req, resp);
    }

    // Construye un objeto OrdenTrabajo con los parámetros que vienen del formulario de cabecera
    private OrdenTrabajo construirCabeceraDesdeRequest(HttpServletRequest req) {
        OrdenTrabajo ot = new OrdenTrabajo();

        ot.setIdOrdenTrabajo(parseLongNullable(req.getParameter("idOrdenTrabajo")));
        ot.setIdCliente(parseLongNullable(req.getParameter("idCliente")));

        String tipoIngresoParam = req.getParameter("tipoIngreso");
        if (tipoIngresoParam != null && !tipoIngresoParam.isBlank()) {
            ot.setTipoIngreso(TipoIngresoOrdenEnum.valueOf(tipoIngresoParam));
        }

        ot.setIdVehiculo(parseLongNullable(req.getParameter("idVehiculo")));
        ot.setIdComponente(parseLongNullable(req.getParameter("idComponente")));

        String cantidadPicosParam = req.getParameter("cantidadPicos");
        if (cantidadPicosParam != null && !cantidadPicosParam.isBlank()) {
            try {
                ot.setCantidadPicos(Short.parseShort(cantidadPicosParam));
            } catch (NumberFormatException ignored) { }
        }

        String fuenteParam = req.getParameter("fuenteReferencia");
        if (fuenteParam != null && !fuenteParam.isBlank()) {
            ot.setFuenteReferencia(FuenteReferenciaClienteEnum.valueOf(fuenteParam));
        }

        ot.setIdReferidor(parseLongNullable(req.getParameter("idReferidor")));
        ot.setProblemaReportado(req.getParameter("problemaReportado"));
        ot.setObservacionesIngreso(req.getParameter("observacionesIngreso"));

        // Fecha de entrega opcional
        String fechaEntregaParam = req.getParameter("fechaEntrega");
        if (fechaEntregaParam != null && !fechaEntregaParam.isBlank()) {
            try {
                ot.setFechaEntrega(LocalDate.parse(fechaEntregaParam).atStartOfDay());
            } catch (DateTimeParseException ignored) { }
        }

        return ot;
    }

    // Construye un objeto OrdenTrabajoDetalle con los parámetros que vienen del formulario de detalle
    private OrdenTrabajoDetalle construirDetalleDesdeRequest(HttpServletRequest req) {
        OrdenTrabajoDetalle d = new OrdenTrabajoDetalle();

        d.setIdDetalle(parseLongNullable(req.getParameter("idDetalle")));
        d.setIdOrdenTrabajo(parseLongNullable(req.getParameter("idOrdenTrabajo")));
        d.setIdServicio(parseLongNullable(req.getParameter("idServicio")));

        String cantidadParam = req.getParameter("cantidad");
        if (cantidadParam != null && !cantidadParam.isBlank()) {
            try {
                d.setCantidad(new BigDecimal(cantidadParam));
            } catch (NumberFormatException ignored) { }
        }

        String precioParam = req.getParameter("precioUnitario");
        if (precioParam != null && !precioParam.isBlank()) {
            try {
                d.setPrecioUnitario(new BigDecimal(precioParam));
            } catch (NumberFormatException ignored) { }
        }

        String mesesParam = req.getParameter("garantiaMeses");
        if (mesesParam != null && !mesesParam.isBlank()) {
            try {
                d.setGarantiaMeses(Short.parseShort(mesesParam));
            } catch (NumberFormatException ignored) { }
        }

        String diasParam = req.getParameter("garantiaDias");
        if (diasParam != null && !diasParam.isBlank()) {
            try {
                d.setGarantiaDias(Short.parseShort(diasParam));
            } catch (NumberFormatException ignored) { }
        }

        d.setObservaciones(req.getParameter("observaciones"));

        return d;
    }

    // Construye la URL de retorno al listado preservando los filtros activos
    private String construirUrlRetornoListado(HttpServletRequest req) {
        String url = req.getContextPath() + "/ordenes-trabajo?action=listar";

        String estado = req.getParameter("estadoFiltro");
        if (estado != null && !estado.isBlank()) {
            url += "&estado=" + URLEncoder.encode(estado, StandardCharsets.UTF_8);
        }

        String dia = req.getParameter("diaFiltro");
        if (dia != null && !dia.isBlank()) {
            url += "&dia=" + URLEncoder.encode(dia, StandardCharsets.UTF_8);
        }

        return url;
    }

    // Devuelve JSON con los datos necesarios para poblar el modal de cliente
    private void ajaxClientes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ClientePersonaListadoDTO> personas = clienteListadoService.listarPersonas(null, true);
        List<ClienteEmpresaListadoDTO> empresas = clienteListadoService.listarEmpresas(null, true);

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"distritos\":[");
        var distritos = distritoService.listarActivos();
        for (int i = 0; i < distritos.size(); i++) {
            var d = distritos.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"id\":").append(d.getIdDistrito()).append(",\"nombre\":\"").append(esc(d.getNombre())).append("\"}");
        }
        sb.append("],\"localidades\":[");
        var localidades = localidadService.listarActivos();
        for (int i = 0; i < localidades.size(); i++) {
            var l = localidades.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"id\":").append(l.getIdLocalidad()).append(",\"nombre\":\"").append(esc(l.getNombre())).append("\",\"idDistrito\":").append(l.getIdDistrito()).append("}");
        }
        // personas y empresas para refrescar el datalist tras guardar
        sb.append("],\"personas\":[");
        for (int i = 0; i < personas.size(); i++) {
            var p = personas.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"id\":").append(p.getIdCliente()).append(",\"nombre\":\"").append(esc(p.getNombre())).append("\",\"apellido\":\"").append(esc(p.getApellido())).append("\",\"apodo\":\"").append(esc(p.getApodo())).append("\"}");
        }
        sb.append("],\"empresas\":[");
        for (int i = 0; i < empresas.size(); i++) {
            var e = empresas.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"id\":").append(e.getIdCliente()).append(",\"razonSocial\":\"").append(esc(e.getRazonSocial())).append("\",\"nombreFantasia\":\"").append(esc(e.getNombreFantasia())).append("\"}");
        }
        sb.append("]}");

        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(sb.toString());
    }

    // Devuelve JSON con marcas, modelos y tipos para poblar el modal de vehículo
    private void ajaxVehiculos(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var marcas   = marcaDAO.listarActivos();
        var modelos  = modeloDAO.listarActivos();
        var vehiculos = vehiculoService.listarActivos();

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"marcas\":[");
        for (int i = 0; i < marcas.size(); i++) {
            var m = marcas.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"id\":").append(m.getIdMarca()).append(",\"nombre\":\"").append(esc(m.getNombre())).append("\"}");
        }
        sb.append("],\"modelos\":[");
        for (int i = 0; i < modelos.size(); i++) {
            var mo = modelos.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"id\":").append(mo.getIdModelo()).append(",\"nombre\":\"").append(esc(mo.getNombre())).append("\",\"idMarca\":").append(mo.getIdMarca()).append("}");
        }
        sb.append("],\"tiposVehiculo\":[");
        var tipos = TipoVehiculoEnum.values();
        for (int i = 0; i < tipos.length; i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(tipos[i].name()).append("\"");
        }
        sb.append("],\"vehiculos\":[");
        for (int i = 0; i < vehiculos.size(); i++) {
            var v = vehiculos.get(i);
            if (i > 0) sb.append(",");
            String label = (v.getPlaca() != null ? v.getPlaca() : "") + (v.getNombreMarca() != null ? " — " + v.getNombreMarca() : "") + (v.getNombreModelo() != null ? " " + v.getNombreModelo() : "");
            sb.append("{\"id\":").append(v.getIdVehiculo()).append(",\"label\":\"").append(esc(label)).append("\"}");
        }
        sb.append("]}");

        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(sb.toString());
    }

    // Devuelve JSON con tipos de componente, marcas, modelos para poblar el modal de componente
    private void ajaxComponentes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var tiposComp = tipoComponenteService.listarActivos();
        var marcas    = marcaDAO.listarActivos();
        var modelos   = modeloDAO.listarActivos();
        var componentes = componenteService.listarActivos();

        StringBuilder sb = new StringBuilder("{");
        sb.append("\"tiposComponente\":[");
        for (int i = 0; i < tiposComp.size(); i++) {
            var tc = tiposComp.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"id\":").append(tc.getIdTipoComponente()).append(",\"nombre\":\"").append(esc(tc.getNombre())).append("\"}");
        }
        sb.append("],\"marcas\":[");
        for (int i = 0; i < marcas.size(); i++) {
            var m = marcas.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"id\":").append(m.getIdMarca()).append(",\"nombre\":\"").append(esc(m.getNombre())).append("\"}");
        }
        sb.append("],\"modelos\":[");
        for (int i = 0; i < modelos.size(); i++) {
            var mo = modelos.get(i);
            if (i > 0) sb.append(",");
            sb.append("{\"id\":").append(mo.getIdModelo()).append(",\"nombre\":\"").append(esc(mo.getNombre())).append("\",\"idMarca\":").append(mo.getIdMarca()).append("}");
        }
        sb.append("],\"componentes\":[");
        for (int i = 0; i < componentes.size(); i++) {
            var c = componentes.get(i);
            if (i > 0) sb.append(",");
            String label = (c.getNumeroSerie() != null ? c.getNumeroSerie() : "") + (c.getNombreMarca() != null ? " — " + c.getNombreMarca() : "") + (c.getNombreModelo() != null ? " " + c.getNombreModelo() : "");
            sb.append("{\"id\":").append(c.getIdComponente()).append(",\"label\":\"").append(esc(label)).append("\"}");
        }
        sb.append("]}");

        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(sb.toString());
    }

    // Guarda un cliente nuevo desde el modal y devuelve JSON con el id y la etiqueta del nuevo registro
    private void ajaxGuardarCliente(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            HttpSession sesion = req.getSession(false);
            SesionDeUsuario usuario = (SesionDeUsuario) sesion.getAttribute("usuarioSesion");

            Cliente c = new Cliente();
            c.setIdUsuarioCreador(usuario.getIdUsuario());
            c.setTelefono(nvl(req.getParameter("telefono")));
            c.setIdDistrito(parseLongNullable(req.getParameter("idDistrito")));
            c.setIdLocalidad(parseLongNullable(req.getParameter("idLocalidad")));
            c.setActivo(true);

            Long idCreado = clienteService.guardar(c);
            String tipo = req.getParameter("tipo");
            String etiqueta;

            if ("PERSONA".equalsIgnoreCase(tipo)) {
                ClientePersona p = new ClientePersona();
                p.setIdCliente(idCreado);
                p.setNombre(req.getParameter("nombre"));
                p.setApellido(req.getParameter("apellido"));
                p.setApodo(nvl(req.getParameter("apodo")));
                clientePersonaService.guardar(p);
                etiqueta = p.getNombre() + " " + p.getApellido() + (p.getApodo() != null ? " (" + p.getApodo() + ")" : "");
            } else {
                ClienteEmpresa e = new ClienteEmpresa();
                e.setIdCliente(idCreado);
                e.setRazonSocial(req.getParameter("razonSocial"));
                e.setNombreFantasia(nvl(req.getParameter("nombreFantasia")));
                clienteEmpresaService.guardar(e);
                etiqueta = e.getRazonSocial() + (e.getNombreFantasia() != null ? " (" + e.getNombreFantasia() + ")" : "");
            }

            out.write("{\"ok\":true,\"id\":" + idCreado + ",\"etiqueta\":\"" + esc(etiqueta) + "\"}");
        } catch (Exception ex) {
            out.write("{\"ok\":false,\"error\":\"" + esc(ex.getMessage()) + "\"}");
        }
    }

    // Guarda un vehículo nuevo desde el modal y devuelve JSON con el id y la etiqueta del nuevo registro
    private void ajaxGuardarVehiculo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            Vehiculo v = new Vehiculo();
            v.setPlaca(nvl(req.getParameter("placa")));
            v.setIdMarca(parseLong(req.getParameter("idMarca")));
            v.setIdModelo(parseLongNullable(req.getParameter("idModelo")));
            String anioStr = req.getParameter("anio");
            if (anioStr != null && !anioStr.isBlank()) {
                try { v.setAnio(Short.parseShort(anioStr)); } catch (NumberFormatException ignored) {}
            }
            String tipoStr = req.getParameter("tipoVehiculo");
            if (tipoStr != null && !tipoStr.isBlank()) {
                try { v.setTipoVehiculo(TipoVehiculoEnum.valueOf(tipoStr)); } catch (IllegalArgumentException ignored) {}
            }
            v.setObservaciones(nvl(req.getParameter("observaciones")));

            Long idCreado = vehiculoService.crear(v);

            // Busca los nombres para armar la etiqueta
            var vGuardado = vehiculoService.buscarPorId(idCreado);
            String etiqueta = vGuardado.map(vv ->
                (vv.getPlaca() != null ? vv.getPlaca() : "") +
                (vv.getNombreMarca() != null ? " — " + vv.getNombreMarca() : "") +
                (vv.getNombreModelo() != null ? " " + vv.getNombreModelo() : "")
            ).orElse("Vehículo " + idCreado);

            out.write("{\"ok\":true,\"id\":" + idCreado + ",\"etiqueta\":\"" + esc(etiqueta) + "\"}");
        } catch (Exception ex) {
            out.write("{\"ok\":false,\"error\":\"" + esc(ex.getMessage()) + "\"}");
        }
    }

    // Guarda un componente nuevo desde el modal y devuelve JSON con el id y la etiqueta del nuevo registro
    private void ajaxGuardarComponente(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        try {
            Componente c = new Componente();
            c.setIdTipoComponente(parseLong(req.getParameter("idTipoComponente")));
            c.setIdMarca(parseLong(req.getParameter("idMarca")));
            c.setIdModelo(parseLong(req.getParameter("idModelo")));
            c.setNumeroSerie(nvl(req.getParameter("numeroSerie")));
            c.setObservaciones(nvl(req.getParameter("observaciones")));

            Long idCreado = componenteService.crear(c);

            var cGuardado = componenteService.buscarPorId(idCreado);
            String etiqueta = cGuardado.map(cc ->
                (cc.getNumeroSerie() != null ? cc.getNumeroSerie() : "S/N") +
                (cc.getNombreMarca() != null ? " — " + cc.getNombreMarca() : "") +
                (cc.getNombreModelo() != null ? " " + cc.getNombreModelo() : "")
            ).orElse("Componente " + idCreado);

            out.write("{\"ok\":true,\"id\":" + idCreado + ",\"etiqueta\":\"" + esc(etiqueta) + "\"}");
        } catch (Exception ex) {
            out.write("{\"ok\":false,\"error\":\"" + esc(ex.getMessage()) + "\"}");
        }
    }

    // Escapa caracteres especiales JSON en strings para evitar romper el JSON manual
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    // Devuelve null si el string está vacío, o el string sin espacios si tiene contenido
    private String nvl(String s) {
        if (s == null || s.isBlank()) return null;
        return s.trim();
    }

    // Parsea un Long obligatorio desde el request, lanza excepción si falta o es inválido
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

    // Parsea un Long opcional desde el request, devuelve null si falta o es inválido
    private Long parseLongNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            Long n = Long.valueOf(value);
            return n <= 0 ? null : n;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
