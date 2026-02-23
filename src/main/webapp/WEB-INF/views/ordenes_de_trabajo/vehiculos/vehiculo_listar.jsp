<%-- 
    Document   : vehiculo_listar
    Created on : 23 feb. 2026, 8:42:09 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Vehículos</title>

    <style>
        .switch {
            display: inline-block;
            width: 46px;
            height: 24px;
            border-radius: 999px;
            position: relative;
            vertical-align: middle;
            text-decoration: none;
            border: 1px solid #999;
            background: #ddd;
        }
        .switch::after {
            content: "";
            position: absolute;
            top: 3px;
            left: 3px;
            width: 18px;
            height: 18px;
            border-radius: 50%;
            background: #fff;
            border: 1px solid #999;
            transition: left 0.15s ease-in-out;
        }
        .switch.on  { background: #4CAF50; border-color: #3E8E41; }
        .switch.on::after { left: 24px; }
        .switch.off { background: #e74c3c; border-color: #c0392b; }
        .switch.off::after { border-color: #c0392b; }
        .switch:hover { filter: brightness(0.95); }
    </style>
</head>
<body>

<p><a href="${pageContext.request.contextPath}/">Volver al inicio</a></p>

<h2>Vehículos</h2>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">${error}</div>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/vehiculos?action=nuevo">Nuevo Vehículo</a>
</p>

<form style="margin: 10px 0 15px 0;" method="get" action="${pageContext.request.contextPath}/vehiculos">
    <input type="hidden" name="action" value="buscar" />
    <label>Buscar por placa:</label>
    <input type="text" name="filtro" value="${filtro}" placeholder="Placa..." style="padding:6px; width:200px;" />
    <button type="submit" style="padding:6px 10px; margin-left:6px;">Filtrar</button>
    <a href="${pageContext.request.contextPath}/vehiculos?action=listar" style="padding:6px 10px; margin-left:6px;">Limpiar</a>
</form>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Placa</th>
            <th>Marca</th>
            <th>Modelo</th>
            <th>Año</th>
            <th>Tipo</th>
            <th>Observaciones</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="v" items="${vehiculos}">
            <tr>
                <td>${v.idVehiculo}</td>
                <td>${not empty v.placa ? v.placa : '-'}</td>
                <td>${v.nombreMarca}</td>
                <td>${not empty v.nombreModelo ? v.nombreModelo : '-'}</td>
                <td>${not empty v.anio ? v.anio : '-'}</td>
                <td>${v.tipoVehiculo}</td>
                <td>${not empty v.observaciones ? v.observaciones : '-'}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/vehiculos?action=editar&id=${v.idVehiculo}">Editar</a>

                    <c:choose>
                        <c:when test="${v.activo}">
                            | <a class="switch on" title="Desactivar"
                                 href="${pageContext.request.contextPath}/vehiculos?action=desactivar&id=${v.idVehiculo}"></a>
                        </c:when>
                        <c:otherwise>
                            | <a class="switch off" title="Activar"
                                 href="${pageContext.request.contextPath}/vehiculos?action=activar&id=${v.idVehiculo}"></a>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

</body>
</html>