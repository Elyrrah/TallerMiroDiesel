<%-- 
    Document   : componente_listar
    Created on : 23 feb. 2026, 8:59:48 a. m.
    Author     : elyrr
--%>
E
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Componentes</title>

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

<h2>Componentes</h2>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">${error}</div>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/componentes?action=nuevo">Nuevo Componente</a>
</p>

<form style="margin: 10px 0 15px 0;" method="get" action="${pageContext.request.contextPath}/componentes">
    <input type="hidden" name="action" value="buscar" />
    <label>Buscar por número de serie:</label>
    <input type="text" name="filtro" value="${filtro}" placeholder="Nro. de serie..." style="padding:6px; width:220px;" />
    <button type="submit" style="padding:6px 10px; margin-left:6px;">Filtrar</button>
    <a href="${pageContext.request.contextPath}/componentes?action=listar" style="padding:6px 10px; margin-left:6px;">Limpiar</a>
</form>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Tipo</th>
            <th>Marca</th>
            <th>Modelo</th>
            <th>Nro. Serie</th>
            <th>Observaciones</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="c" items="${componentes}">
            <tr>
                <td>${c.idComponente}</td>
                <td>${c.nombreTipoComponente}</td>
                <td>${c.nombreMarca}</td>
                <td>${c.nombreModelo}</td>
                <td>${not empty c.numeroSerie ? c.numeroSerie : '-'}</td>
                <td>${not empty c.observaciones ? c.observaciones : '-'}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/componentes?action=editar&id=${c.idComponente}">Editar</a>

                    <c:choose>
                        <c:when test="${c.activo}">
                            | <a class="switch on" title="Desactivar"
                                 href="${pageContext.request.contextPath}/componentes?action=desactivar&id=${c.idComponente}"></a>
                        </c:when>
                        <c:otherwise>
                            | <a class="switch off" title="Activar"
                                 href="${pageContext.request.contextPath}/componentes?action=activar&id=${c.idComponente}"></a>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

</body>
</html>