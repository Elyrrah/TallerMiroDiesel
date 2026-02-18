<%-- 
    Document   : distrito_form
    Created on : 21 ene. 2026, 11:18:13 a. m.
    Author     : elyrr
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Formulario Distrito</title>
    </head>
    <body>
        <c:set var="esEdicion" value="${not empty distrito.idDistrito}" />

        <h1>
            <c:choose>
                <c:when test="${esEdicion}">Editar Distrito</c:when>
                <c:otherwise>Nuevo Distrito</c:otherwise>
            </c:choose>
        </h1>

        <c:if test="${not empty error}">
            <div style="color: red; margin-bottom: 10px;">
                ${error}
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/distritos">
            <input type="hidden" name="action" value="guardar"/>

            <c:if test="${esEdicion}">
                <input type="hidden" name="idDistrito" value="${distrito.idDistrito}"/>
            </c:if>

            <div style="margin-bottom: 10px;">
                <label>Departamento:</label><br/>
                <select name="idDepartamento" required>
                    <option value="">-- Seleccione un departamento --</option>
                    <c:forEach var="d" items="${departamentos}">
                        <option value="${d.idDepartamento}"
                                <c:if test="${d.idDepartamento == distrito.idDepartamento}">selected</c:if>>
                            ${d.nombre}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div style="margin-bottom: 10px;">
                <label>Nombre del Distrito:</label><br/>
                <input type="text" name="nombre" value="${distrito.nombre}" required/>
            </div>

            <c:if test="${esEdicion}">
                <div style="margin-bottom: 10px;">
                    <label>Activo:</label><br/>
                    <select name="activo">
                        <option value="true" ${distrito.activo ? 'selected' : ''}>Sí</option>
                        <option value="false" ${!distrito.activo ? 'selected' : ''}>No</option>
                    </select>
                </div>
            </c:if>

            <button type="submit">
                <c:choose>
                    <c:when test="${esEdicion}">Guardar cambios</c:when>
                    <c:otherwise>Crear</c:otherwise>
                </c:choose>
            </button>
            <a style="margin-left: 10px;" href="${pageContext.request.contextPath}/distritos?action=listar">
                Volver
            </a>
        </form>
    </body>
</html>