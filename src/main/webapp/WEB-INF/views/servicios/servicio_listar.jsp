<%-- 
    Document   : servicio_listar
    Created on : 27 ene. 2026, 10:47:50 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Servicios</title>
</head>
<body>

<h2>Catálogo de Servicios</h2>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/servicios?accion=nuevo">
        Nuevo Servicio
    </a>
</p>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Código</th>
            <th>Nombre</th>
            <th>Precio Base</th>
            <th>Activo</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="s" items="${servicios}">
            <tr>
                <td>${s.idServicio}</td>
                <td>${s.codigo}</td>
                <td>${s.nombre}</td>

                <td>
                    ₲ <fmt:formatNumber value="${s.precioBase}" pattern="#,##0" />
                </td>


                <td>${s.activo}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/servicios?accion=editar&id=${s.idServicio}">
                        Editar
                    </a>

                    <c:choose>
                        <c:when test="${s.activo}">
                            | <a href="${pageContext.request.contextPath}/servicios?accion=desactivar&id=${s.idServicio}">
                                Desactivar
                              </a>
                        </c:when>
                        <c:otherwise>
                            | <a href="${pageContext.request.contextPath}/servicios?accion=activar&id=${s.idServicio}">
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