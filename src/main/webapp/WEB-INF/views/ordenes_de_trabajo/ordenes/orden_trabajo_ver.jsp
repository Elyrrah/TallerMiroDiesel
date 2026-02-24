<%-- 
    Document   : orden_trabajo_ver
    Created on : 23 feb. 2026, 1:51:15 p. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Orden de Trabajo — ${ot.numeroOrden}</title>
</head>
<body>

<p><a href="${pageContext.request.contextPath}/">Volver al inicio</a></p>
<p><a href="${pageContext.request.contextPath}/ordenes-trabajo?action=listar">Volver al listado</a></p>

<%-- Mensaje de error --%>
<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">${error}</div>
</c:if>

<h2>Orden de Trabajo: ${ot.numeroOrden}</h2>

<%-- Cabecera de la OT --%>
<table border="1" cellpadding="6" cellspacing="0">
    <tr>
        <th>Estado</th>
        <td>${ot.estado}</td>
        <th>Registrado por</th>
        <td>${ot.nombreUsuario}</td>
    </tr>
    <tr>
        <th>Fecha Ingreso</th>
        <td>${ot.fechaIngreso}</td>
        <th>Fecha Entrega Estimada</th>
        <td>${not empty ot.fechaEntrega ? ot.fechaEntrega : '—'}</td>
    </tr>
    <tr>
        <th>Cliente</th>
        <td>${ot.nombreCliente}</td>
        <th>Referido por</th>
        <td>
            <c:choose>
                <c:when test="${ot.fuenteReferencia == 'NINGUNA'}">—</c:when>
                <c:otherwise>${ot.fuenteReferencia}: ${ot.nombreReferidor}</c:otherwise>
            </c:choose>
        </td>
    </tr>
    <tr>
        <th>Tipo de Ingreso</th>
        <td>${ot.tipoIngreso}</td>
        <th>Vehículo / Componente</th>
        <td>
            <c:choose>
                <c:when test="${ot.tipoIngreso == 'VEHICULO'}">
                    ${ot.placaVehiculo} — ${ot.marcaVehiculo} ${ot.modeloVehiculo}
                </c:when>
                <c:otherwise>
                    ${ot.numeroSerieComponente} — ${ot.marcaComponente} ${ot.modeloComponente}
                    <c:if test="${not empty ot.cantidadPicos}">
                        (${ot.cantidadPicos} picos)
                    </c:if>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
    <tr>
        <th>Problema Reportado</th>
        <td colspan="3">${not empty ot.problemaReportado ? ot.problemaReportado : '—'}</td>
    </tr>
    <tr>
        <th>Observaciones</th>
        <td colspan="3">${not empty ot.observacionesIngreso ? ot.observacionesIngreso : '—'}</td>
    </tr>
</table>

<%-- Botón editar cabecera — solo si la OT no está cerrada --%>
<c:if test="${ot.estado != 'FINALIZADA' && ot.estado != 'ENTREGADA' && ot.estado != 'CANCELADA'}">
    <p>
        <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=editar&id=${ot.idOrdenTrabajo}">
            Editar cabecera
        </a>
    </p>
</c:if>

<br/>

<%-- Tabla de servicios (detalles) --%>
<h3>Servicios Realizados</h3>

<%-- Botón agregar servicio — solo si la OT no está cerrada --%>
<c:if test="${ot.estado != 'FINALIZADA' && ot.estado != 'ENTREGADA' && ot.estado != 'CANCELADA'}">
    <p>
        <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=nuevoDetalle&idOrden=${ot.idOrdenTrabajo}">
            + Agregar Servicio
        </a>
    </p>
</c:if>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>Servicio (ID)</th>
            <th>Cantidad</th>
            <th>Precio Unitario</th>
            <th>Subtotal</th>
            <th>Garantía</th>
            <th>Observaciones</th>
            <c:if test="${ot.estado != 'FINALIZADA' && ot.estado != 'ENTREGADA' && ot.estado != 'CANCELADA'}">
                <th>Acciones</th>
            </c:if>
        </tr>
    </thead>
    <tbody>

        <c:if test="${empty detalles}">
            <tr>
                <td colspan="7">Aún no se han cargado servicios en esta orden.</td>
            </tr>
        </c:if>

        <c:forEach var="d" items="${detalles}">
            <tr>
                <%-- El modelo limpio solo tiene idServicio; el nombre viene del catálogo --%>
                <td>${d.idServicio}</td>
                <td>${d.cantidad}</td>
                <td><fmt:formatNumber value="${d.precioUnitario}" pattern="#,##0" /> ₲</td>
                <td><fmt:formatNumber value="${d.subtotal}" pattern="#,##0" /> ₲</td>
                <td>
                    <c:choose>
                        <c:when test="${not empty d.garantiaMeses || not empty d.garantiaDias}">
                            <c:if test="${not empty d.garantiaMeses}">${d.garantiaMeses} mes(es) </c:if>
                            <c:if test="${not empty d.garantiaDias}">${d.garantiaDias} día(s)</c:if>
                        </c:when>
                        <c:otherwise>—</c:otherwise>
                    </c:choose>
                </td>
                <td>${not empty d.observaciones ? d.observaciones : '—'}</td>
                <c:if test="${ot.estado != 'FINALIZADA' && ot.estado != 'ENTREGADA' && ot.estado != 'CANCELADA'}">
                    <td>
                        <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=editarDetalle&idDetalle=${d.idDetalle}">
                            Editar
                        </a>
                        | <a href="${pageContext.request.contextPath}/ordenes-trabajo?action=quitarDetalle&idDetalle=${d.idDetalle}&idOrden=${ot.idOrdenTrabajo}"
                             onclick="return confirm('¿Quitar este servicio de la orden?')">
                            Quitar
                          </a>
                    </td>
                </c:if>
            </tr>
        </c:forEach>

    </tbody>
</table>

<br/>

<%-- Totales --%>
<table border="1" cellpadding="6" cellspacing="0">
    <tr>
        <th>Total Trabajo</th>
        <td><fmt:formatNumber value="${ot.totalTrabajo}" pattern="#,##0" /> ₲</td>
    </tr>
    <tr>
        <th>Total Pagado</th>
        <td><fmt:formatNumber value="${ot.totalPagado}" pattern="#,##0" /> ₲</td>
    </tr>
    <tr>
        <th>Saldo Pendiente</th>
        <td><fmt:formatNumber value="${ot.saldoPendiente}" pattern="#,##0" /> ₲</td>
    </tr>
    <tr>
        <th>Estado de Pago</th>
        <td>${ot.estadoPago}</td>
    </tr>
</table>

</body>
</html>
