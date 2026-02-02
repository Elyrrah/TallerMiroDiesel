<%-- 
    Document   : localidad_form
    Created on : 21 ene. 2026, 4:01:25 p. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Formulario Localidad</title>
    </head>
    <body>

        <h1>
            <c:choose>
                <c:when test="${not empty localidad.idLocalidad}">Editar Localidad</c:when>
                <c:otherwise>Nueva Localidad</c:otherwise>
            </c:choose>
        </h1>

        <c:if test="${not empty error}">
            <div style="color: red;">
                ${error}
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/localidades">
            <input type="hidden" name="accion" value="guardar"/>

            <%-- Si existe, se manda para actualizar --%>
            <input type="hidden" name="idLocalidad" value="${localidad.idLocalidad}"/>

            <p>
                <label>Distrito:</label>
                <select name="idDistrito" required>
                    <option value="">-- Seleccione --</option>
                    <c:forEach var="d" items="${distritos}">
                        <option value="${d.idDistrito}"
                                <c:if test="${not empty localidad.idDistrito and localidad.idDistrito == d.idDistrito}">selected</c:if>>
                            ${d.nombre}
                        </option>
                    </c:forEach>
                </select>
            </p>

            <p>
                <label>Nombre:</label>
                <input type="text" name="nombre" value="${localidad.nombre}" required/>
            </p>

            <p>
                <label>Activo:</label>
                <input type="checkbox" name="activo"
                       <c:if test="${empty localidad.idLocalidad or localidad.activo}">checked</c:if> />
            </p>

            <button type="submit">Guardar</button>
            <a href="${pageContext.request.contextPath}/localidades?accion=listar">Cancelar</a>
        </form>

    </body>
</html>
