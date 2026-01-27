<%-- 
    Document   : tipos_documento_listar
    Created on : 27 ene. 2026, 9:55:28 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tipos de Documento</title>
</head>
<body>

<h2>Catálogo de Tipos de Documento</h2>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/tipos-documento?accion=nuevo">
        Nuevo Tipo de Documento
    </a>
</p>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Código</th>
            <th>Aplica A</th>
            <th>Activo</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="td" items="${tiposDocumento}">
            <tr>
                <td>${td.idTipoDocumento}</td>
                <td>${td.nombre}</td>
                <td>${td.codigo}</td>
                <td>${td.aplicaA}</td>
                <td>${td.activo}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/tipos-documento?accion=editar&id=${td.idTipoDocumento}">
                        Editar
                    </a>

                    <c:choose>
                        <c:when test="${td.activo}">
                            | <a href="${pageContext.request.contextPath}/tipos-documento?accion=desactivar&id=${td.idTipoDocumento}">
                                Desactivar
                              </a>
                        </c:when>
                        <c:otherwise>
                            | <a href="${pageContext.request.contextPath}/tipos-documento?accion=activar&id=${td.idTipoDocumento}">
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
