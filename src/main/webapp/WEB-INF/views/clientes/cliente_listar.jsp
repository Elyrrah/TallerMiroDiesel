<%-- 
    Document   : cliente_listar
    Created on : 2 feb. 2026, 3:37:22 p. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Clientes</title>

    <style>
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 6px; border: 1px solid #000; text-align: left; }
        .section { margin-top: 24px; }
        .muted { color: #555; }
        .filters { margin: 12px 0; padding: 10px; border: 1px solid #ddd; }
    </style>
</head>
<body>

    <%-- =========================
         BLOQUE: Navegación
         ========================= --%>
    <p>
        <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
    </p>

    <h2>Clientes</h2>

    <%-- =========================
         BLOQUE: Errores
         ========================= --%>
    <c:if test="${not empty error}">
        <div style="color:red; margin-bottom:10px;">
            ${error}
        </div>
    </c:if>

    <%-- =========================
         BLOQUE: Acciones
         ========================= --%>
    <p>
        <a href="${pageContext.request.contextPath}/clientes?action=new">Nuevo Cliente</a>
    </p>

    <%-- =========================
         BLOQUE: Filtros (GET)
         ========================= --%>
    <div class="filters">
        <form method="get" action="${pageContext.request.contextPath}/clientes">
            <input type="hidden" name="action" value="list"/>

            Buscar (nombre/teléfono):<br/>
            <input type="text" name="q" value="${q}" style="width:260px;"/>
            <br/><br/>

            Estado:<br/>
            <select name="estado">
                <option value="" <c:if test="${empty estado}">selected</c:if>>TODOS</option>
                <option value="ACTIVOS" <c:if test="${estado == 'ACTIVOS'}">selected</c:if>>ACTIVOS</option>
                <option value="INACTIVOS" <c:if test="${estado == 'INACTIVOS'}">selected</c:if>>INACTIVOS</option>
            </select>

            <button type="submit" style="margin-left:10px;">Filtrar</button>
        </form>
    </div>

    <%-- =========================================================
         TABLA 1: CLIENTES PERSONA (ARRIBA)
         ========================================================= --%>
    <div class="section">
        <h3>Clientes Persona</h3>

        <c:choose>
            <c:when test="${empty listaClientesPersona}">
                <p class="muted">No hay clientes persona.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre y Apellido</th>
                            <th>Apodo</th>
                            <th>Teléfono</th>
                            <th>Distrito</th>
                            <th>Localidad</th>
                            <th>Referidor</th>
                            <th>Fuente</th>
                            <th>Estado</th>
                            <th>Fecha Creación</th>
                            <th>Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${listaClientesPersona}">
                            <tr>
                                <td>${c.idCliente}</td>
                                <td>${c.nombreCompleto}</td>
                                <td>${c.apodo}</td>
                                <td>${c.telefono}</td>
                                <td>${c.nombreDistrito}</td>
                                <td>${c.nombreLocalidad}</td>
                                <td>${c.nombreReferidor}</td>
                                <td>${c.fuenteReferencia}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${c.activo}">ACTIVO</c:when>
                                        <c:otherwise>INACTIVO</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${c.fechaCreacion}</td>
                                <td>
                                    <%-- Toggle único Activar/Desactivar --%>
                                    <form method="post" action="${pageContext.request.contextPath}/clientes" style="margin:0;">
                                        <input type="hidden" name="action" value="toggleActivo"/>
                                        <input type="hidden" name="id" value="${c.idCliente}"/>
                                        <input type="hidden" name="activo_actual" value="${c.activo}"/>

                                        <%-- Preservar filtros al volver --%>
                                        <input type="hidden" name="q" value="${q}"/>
                                        <input type="hidden" name="estado" value="${estado}"/>

                                        <button type="submit">
                                            <c:choose>
                                                <c:when test="${c.activo}">Desactivar</c:when>
                                                <c:otherwise>Activar</c:otherwise>
                                            </c:choose>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- =========================================================
         TABLA 2: CLIENTES EMPRESA (ABAJO)
         ========================================================= --%>
    <div class="section">
        <h3>Clientes Empresa</h3>

        <c:choose>
            <c:when test="${empty listaClientesEmpresa}">
                <p class="muted">No hay clientes empresa.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Empresa</th>
                            <th>Nombre Fantasía</th>
                            <th>Teléfono</th>
                            <th>ID Distrito</th>
                            <th>ID Localidad</th>
                            <th>ID Referidor</th>
                            <th>Fuente</th>
                            <th>Estado</th>
                            <th>Fecha Creación</th>
                            <th>Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${listaClientesEmpresa}">
                            <tr>
                                <td>${c.idCliente}</td>
                                <td>${c.nombre}</td>
                                <td>${c.apodoNombreFantasia}</td>
                                <td>${c.telefono}</td>
                                <td>${c.idDistrito}</td>
                                <td>${c.idLocalidad}</td>
                                <td>${c.idClienteReferidor}</td>
                                <td>${c.fuenteReferencia}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${c.activo}">ACTIVO</c:when>
                                        <c:otherwise>INACTIVO</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${c.fechaCreacion}</td>
                                <td>
                                    <%-- Toggle único Activar/Desactivar --%>
                                    <form method="post" action="${pageContext.request.contextPath}/clientes" style="margin:0;">
                                        <input type="hidden" name="action" value="toggleActivo"/>
                                        <input type="hidden" name="id" value="${c.idCliente}"/>
                                        <input type="hidden" name="activo_actual" value="${c.activo}"/>

                                        <%-- Preservar filtros al volver --%>
                                        <input type="hidden" name="q" value="${q}"/>
                                        <input type="hidden" name="estado" value="${estado}"/>

                                        <button type="submit">
                                            <c:choose>
                                                <c:when test="${c.activo}">Desactivar</c:when>
                                                <c:otherwise>Activar</c:otherwise>
                                            </c:choose>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

</body>
</html>
