<%-- 
    Document   : marca_listar
    Created on : 26 ene. 2026, 9:34:23 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Marcas</title>
</head>
<body>

<h2>Catálogo de Marcas</h2>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/marcas?accion=nuevo">
        Nueva Marca
    </a>
</p>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Activo</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="m" items="${marcas}">
            <tr>
                <td>${m.idMarca}</td>
                <td>${m.nombre}</td>
                <td>${m.activo}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/marcas?accion=editar&id=${m.idMarca}">
                        Editar
                    </a>

                    <c:choose>
                        <c:when test="${m.activo}">
                            | <a href="${pageContext.request.contextPath}/marcas?accion=desactivar&id=${m.idMarca}">
                                Desactivar
                              </a>
                        </c:when>
                        <c:otherwise>
                            | <a href="${pageContext.request.contextPath}/marcas?accion=activar&id=${m.idMarca}">
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
