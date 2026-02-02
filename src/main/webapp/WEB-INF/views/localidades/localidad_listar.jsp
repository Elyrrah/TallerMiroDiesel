<%-- 
    Document   : localidad_listar
    Created on : 21 ene. 2026, 4:01:11 p. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Localidades</title>
    </head>
    <body>

        <h1>Localidades</h1>

        <c:if test="${not empty error}">
            <div style="color: red;">
                ${error}
            </div>
        </c:if>

        <p>
            <a href="${pageContext.request.contextPath}/localidades?accion=nuevo">Nueva Localidad</a>
        </p>

        <%-- Filtro por Distrito + búsqueda por nombre --%>
        <form method="get" action="${pageContext.request.contextPath}/localidades">
            <input type="hidden" name="accion" value="listar"/>

            <label>Distrito:</label>
            <select name="idDistrito">
                <option value="">-- Todos --</option>
                <c:forEach var="d" items="${distritos}">
                    <option value="${d.idDistrito}"
                            <c:if test="${not empty idDistrito and idDistrito == d.idDistrito}">selected</c:if>>
                        ${d.nombre}
                    </option>
                </c:forEach>
            </select>

            <label>Buscar:</label>
            <input type="text" name="filtro" value="${filtro}" placeholder="Nombre de la localidad"/>

            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/localidades?accion=listar">Limpiar</a>
        </form>

        <br/>

        <table border="1" cellpadding="6" cellspacing="0">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Distrito</th>
                    <th>Nombre</th>
                    <th>Activo</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>

                <c:if test="${empty listaLocalidades}">
                    <tr>
                        <td colspan="5">No hay localidades registradas.</td>
                    </tr>
                </c:if>

                <c:forEach var="l" items="${listaLocalidades}">
                    <tr>
                        <td>${l.idLocalidad}</td>
                        <td>${l.nombreDistrito}</td>
                        <td>${l.nombre}</td>
                        <td>
                            <c:choose>
                                <c:when test="${l.activo}">SI</c:when>
                                <c:otherwise>NO</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/localidades?accion=editar&id=${l.idLocalidad}">Editar</a>

                            <c:choose>
                                <c:when test="${l.activo}">
                                    | <a href="${pageContext.request.contextPath}/localidades?accion=desactivar&id=${l.idLocalidad}">Desactivar</a>
                                </c:when>
                                <c:otherwise>
                                    | <a href="${pageContext.request.contextPath}/localidades?accion=activar&id=${l.idLocalidad}">Activar</a>
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
