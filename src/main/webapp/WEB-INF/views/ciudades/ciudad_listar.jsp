<%-- 
    Document   : ciudad_listar
    Created on : 21 ene. 2026, 11:18:30 a. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Ciudades</title>
    </head>
    <body>

        <h1>Ciudades</h1>

        <c:if test="${not empty error}">
            <div style="color: red;">
                ${error}
            </div>
        </c:if>

        <p>
            <a href="${pageContext.request.contextPath}/ciudades?accion=nuevo">Nueva Ciudad</a>
        </p>

        <%-- Filtro por Departamento (opcional) --%>
        <form method="get" action="${pageContext.request.contextPath}/ciudades">
            <input type="hidden" name="accion" value="listar"/>

            <label>Departamento:</label>
            <select name="idDepartamento">
                <option value="">-- Todos --</option>
                <c:forEach var="d" items="${departamentos}">
                    <option value="${d.idDepartamento}"
                            <c:if test="${not empty idDepartamento and idDepartamento == d.idDepartamento}">selected</c:if>>
                        ${d.nombre}
                    </option>
                </c:forEach>
            </select>

            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/ciudades?accion=listar">Limpiar</a>
        </form>

        <br/>

        <table border="1" cellpadding="6" cellspacing="0">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Departamento</th>
                    <th>Nombre</th>
                    <th>Activo</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>

                <c:if test="${empty lista}">
                    <tr>
                        <td colspan="5">No hay ciudades registradas.</td>
                    </tr>
                </c:if>

                <c:forEach var="c" items="${lista}">
                    <tr>
                        <td>${c.idCiudad}</td>
                        <td>${c.nombreDepartamento}</td>
                        <td>${c.nombre}</td>
                        <td>
                            <c:choose>
                                <c:when test="${c.activo}">SI</c:when>
                                <c:otherwise>NO</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/ciudades?accion=editar&id=${c.idCiudad}">Editar</a>

                            <c:choose>
                                <c:when test="${c.activo}">
                                    | <a href="${pageContext.request.contextPath}/ciudades?accion=desactivar&id=${c.idCiudad}">Desactivar</a>
                                </c:when>
                                <c:otherwise>
                                    | <a href="${pageContext.request.contextPath}/ciudades?accion=activar&id=${c.idCiudad}">Activar</a>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>

            </tbody>
        </table>

    </body>
</html>
