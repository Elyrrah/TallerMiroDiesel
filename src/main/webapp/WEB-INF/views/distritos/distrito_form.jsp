<%-- 
    Document   : distrito_form
    Created on : 21 ene. 2026, 4:01:25 p. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Formulario Distrito</title>
    </head>
    <body>

        <h1>
            <c:choose>
                <c:when test="${not empty distrito.idDistrito}">Editar Distrito</c:when>
                <c:otherwise>Nuevo Distrito</c:otherwise>
            </c:choose>
        </h1>

        <c:if test="${not empty error}">
            <div style="color: red;">
                ${error}
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/distritos">
            <input type="hidden" name="accion" value="guardar"/>

            <%-- Si existe, se manda para actualizar --%>
            <input type="hidden" name="idDistrito" value="${distrito.idDistrito}"/>

            <p>
                <label>Ciudad:</label>
                <select name="idCiudad" required>
                    <option value="">-- Seleccione --</option>
                    <c:forEach var="c" items="${ciudades}">
                        <option value="${c.idCiudad}"
                                <c:if test="${not empty distrito.idCiudad and distrito.idCiudad == c.idCiudad}">selected</c:if>>
                            ${c.nombre}
                        </option>
                    </c:forEach>
                </select>
            </p>

            <p>
                <label>Nombre:</label>
                <input type="text" name="nombre" value="${distrito.nombre}" required/>
            </p>

            <p>
                <label>Activo:</label>
                <input type="checkbox" name="activo"
                       <c:if test="${empty distrito.idDistrito or distrito.activo}">checked</c:if> />
            </p>

            <button type="submit">Guardar</button>
            <a href="${pageContext.request.contextPath}/distritos?accion=listar">Cancelar</a>
        </form>

    </body>
</html>
