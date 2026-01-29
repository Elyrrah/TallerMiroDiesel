<%-- 
    Document   : distrito_listar
    Created on : 21 ene. 2026, 4:01:11 p. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Distritos</title>
    </head>
    <body>

        <h1>Distritos</h1>

        <c:if test="${not empty error}">
            <div style="color: red;">
                ${error}
            </div>
        </c:if>

        <p>
            <a href="${pageContext.request.contextPath}/distritos?accion=nuevo">Nuevo Distrito</a>
        </p>
        <c:if test="${not empty q}">
            <p>Resultados para: <b>${q}</b></p>
        </c:if>

        <%-- Filtro por Ciudad + Búsqueda por nombre (opcional) --%>
        <form method="get" action="${pageContext.request.contextPath}/distritos">
            <input type="hidden" name="accion" value="listar"/>

            <label>Ciudad:</label>
            <select name="idCiudad">
                <option value="">-- Todos --</option>
                <c:forEach var="c" items="${ciudades}">
                    <option value="${c.idCiudad}"
                            <c:if test="${not empty idCiudad and idCiudad == c.idCiudad}">selected</c:if>>
                        ${c.nombre}
                    </option>
                </c:forEach>
            </select>

            <label>Buscar:</label>
            <input type="text" name="q" value="${q}" placeholder="Nombre del distrito"/>

            <button type="submit">Filtrar</button>
            <a href="${pageContext.request.contextPath}/distritos?accion=listar">Limpiar</a>
        </form>

        <br/>

        <table border="1" cellpadding="6" cellspacing="0">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Ciudad</th>
                    <th>Nombre</th>
                    <th>Activo</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>

                <c:if test="${empty listaDistritos}">
                    <tr>
                        <td colspan="5">No hay distritos registrados.</td>
                    </tr>
                </c:if>

                <c:forEach var="d" items="${listaDistritos}">
                    <tr>
                        <td>${d.idDistrito}</td>
                        <td>${d.nombreCiudad}</td>
                        <td>${d.nombre}</td>
                        <td>
                            <c:choose>
                                <c:when test="${d.activo}">SI</c:when>
                                <c:otherwise>NO</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/distritos?accion=editar&id=${d.idDistrito}">Editar</a>

                            <c:choose>
                                <c:when test="${d.activo}">
                                    | <a href="${pageContext.request.contextPath}/distritos?accion=desactivar&id=${d.idDistrito}">Desactivar</a>
                                </c:when>
                                <c:otherwise>
                                    | <a href="${pageContext.request.contextPath}/distritos?accion=activar&id=${d.idDistrito}">Activar</a>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>

            </tbody>
        </table>

    </body>
</html>
