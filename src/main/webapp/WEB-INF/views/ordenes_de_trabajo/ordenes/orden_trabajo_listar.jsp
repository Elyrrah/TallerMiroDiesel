<%-- 
    Document   : orden_trabajo_listar
    Created on : 23 feb. 2026, 1:44:36 p. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Órdenes de Trabajo</title>
</head>
<body>

<p><a href="${pageContext.request.contextPath}/">Volver al inicio</a></p>

<h2>Órdenes de Trabajo</h2>

<%-- Mensaje de error --%>
<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">${error}</div>
</c:if>

<%-- Botón nueva OT --%>
<p>
    <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=nuevo">Nueva Orden de Trabajo</a>
</p>

<%-- Filtros por estado y día --%>
<form method="get" action="${pageContext.request.contextPath}/ordenes-trabajo">
    <input type="hidden" name="action" value="buscar" />

    <label>Estado:</label>
    <select name="estado">
        <option value="">-- Todos --</option>
        <c:forEach var="e" items="${estados}">
            <option value="${e}" ${estadoFiltro == e.name() ? 'selected' : ''}>${e}</option>
        </c:forEach>
    </select>

    &nbsp;
    <label>Día:</label>
    <input type="date" name="dia" value="${diaFiltro}" />

    &nbsp;
    <button type="submit">Filtrar</button>
    <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=listar">Limpiar</a>
</form>

<br/>

<%-- Tabla de OTs --%>
<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>Número</th>
            <th>Fecha Ingreso</th>
            <th>Cliente</th>
            <th>Tipo</th>
            <th>Vehículo / Componente</th>
            <th>Estado</th>
            <th>Estado Pago</th>
            <th>Total</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>

        <c:if test="${empty ordenes}">
            <tr>
                <td colspan="9">No hay órdenes de trabajo para mostrar.</td>
            </tr>
        </c:if>

        <c:forEach var="ot" items="${ordenes}">
            <tr>
                <td>${ot.numeroOrden}</td>
                <td>${ot.fechaIngreso}</td>
                <td>${ot.nombreCliente}</td>
                <td>${ot.tipoIngreso}</td>
                <td>
                    <%-- El ListadoDTO tiene placaVehiculo (solo para VEHICULO) y marcaModelo para ambos --%>
                    <c:choose>
                        <c:when test="${ot.tipoIngreso == 'VEHICULO'}">
                            ${ot.placaVehiculo} — ${ot.marcaModelo}
                        </c:when>
                        <c:otherwise>
                            ${ot.marcaModelo}
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>${ot.estado}</td>
                <td>${ot.estadoPago}</td>
                <td><fmt:formatNumber value="${ot.totalTrabajo}" pattern="#,##0" /> ₲</td>
                <td>
                    <%-- Ver detalle --%>
                    <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=ver&id=${ot.idOrdenTrabajo}">Ver</a>

                    <%-- Editar cabecera --%>
                    | <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=editar&id=${ot.idOrdenTrabajo}">Editar</a>

                    <%-- Activar / Desactivar --%>
                    <c:choose>
                        <c:when test="${ot.activo}">
                            | <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=desactivar&id=${ot.idOrdenTrabajo}"
                                 onclick="return confirm('¿Desactivar esta OT?')">Desactivar</a>
                        </c:when>
                        <c:otherwise>
                            | <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=activar&id=${ot.idOrdenTrabajo}">Activar</a>
                        </c:otherwise>
                    </c:choose>

                    <%-- Eliminar — solo si la OT está inactiva --%>
                    <c:if test="${!ot.activo}">
                        | <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=eliminar&id=${ot.idOrdenTrabajo}"
                             onclick="return confirm('¿Eliminar esta OT? Esta acción no se puede deshacer.')">
                            Eliminar
                          </a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>

    </tbody>
</table>

</body>
</html>
