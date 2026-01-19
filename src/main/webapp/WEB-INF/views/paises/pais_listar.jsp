<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Países</title>
</head>
<body>

<h2>Catálogo de Países</h2>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/paises?action=new">
        Nuevo País
    </a>
</p>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>ISO2</th>
            <th>ISO3</th>
            <th>Activo</th>
            <th>Acciones</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="p" items="${paises}">
            <tr>
                <td>${p.idPais}</td>
                <td>${p.nombre}</td>
                <td>${p.iso2}</td>
                <td>${p.iso3}</td>
                <td>${p.activo}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/paises?action=edit&id=${p.idPais}">
                        Editar
                    </a>

                    <c:choose>
                        <c:when test="${p.activo}">
                            | <a href="${pageContext.request.contextPath}/paises?action=deactivate&id=${p.idPais}">
                                Desactivar
                              </a>
                        </c:when>
                        <c:otherwise>
                            | <a href="${pageContext.request.contextPath}/paises?action=activate&id=${p.idPais}">
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
    <a href="${pageContext.request.contextPath}/index.jsp">Volver al inicio</a>
</p>

</body>
</html>
