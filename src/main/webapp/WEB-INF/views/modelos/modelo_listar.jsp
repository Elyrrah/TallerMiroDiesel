<%-- 
    Document   : modelo_listar
    Created on : 26 ene. 2026, 2:30:23 p. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Modelos</title>
</head>
<body>

<h2>Catálogo de Modelos</h2>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/modelos?accion=nuevo">
        Nuevo Modelo
    </a>
</p>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Marca</th>
            <th>Nombre</th>
            <th>Activo</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="m" items="${listaModelos}">
            <tr>
                <td>${m.idModelo}</td>
                <td>${m.nombreMarca}</td>
                <td>${m.nombre}</td>
                <td>${m.activo}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/modelos?accion=editar&id=${m.idModelo}">
                        Editar
                    </a>

                    <c:choose>
                        <c:when test="${m.activo}">
                            | <a href="${pageContext.request.contextPath}/modelos?accion=desactivar&id=${m.idModelo}">
                                Desactivar
                              </a>
                        </c:when>
                        <c:otherwise>
                            | <a href="${pageContext.request.contextPath}/modelos?accion=activar&id=${m.idModelo}">
                                Activar
                              </a>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<p style="margin-top:15px;">
    <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
</p>

</body>
</html>
