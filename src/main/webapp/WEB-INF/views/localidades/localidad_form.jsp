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
        <meta charset="UTF-8">
        <title>Formulario Localidad</title>
    </head>
    <body>
        <c:set var="esEdicion" value="${not empty localidad.idLocalidad}" />
        
        <h1>
            <c:choose>
                <c:when test="${esEdicion}">Editar Localidad</c:when>
                <c:otherwise>Nueva Localidad</c:otherwise>
            </c:choose>
        </h1>
        
        <c:if test="${not empty error}">
            <div style="color: red; margin-bottom: 10px;">
                ${error}
            </div>
        </c:if>
        
        <form method="post" action="${pageContext.request.contextPath}/localidades">
            <input type="hidden" name="action" value="guardar"/>
            
            <c:if test="${esEdicion}">
                <input type="hidden" name="idLocalidad" value="${localidad.idLocalidad}"/>
            </c:if>
            
            <div style="margin-bottom: 10px;">
                <label>Distrito:</label><br/>
                <select name="idDistrito" required>
                    <option value="">-- Seleccione un distrito --</option>
                    <c:forEach var="d" items="${distritos}">
                        <option value="${d.idDistrito}"
                                <c:if test="${d.idDistrito == localidad.idDistrito}">selected</c:if>>
                            ${d.nombre}
                        </option>
                    </c:forEach>
                </select>
            </div>
            
            <div style="margin-bottom: 10px;">
                <label>Nombre de la Localidad:</label><br/>
                <input type="text" name="nombre" value="${localidad.nombre}" required/>
            </div>
            
            <c:if test="${esEdicion}">
                <div style="margin-bottom: 10px;">
                    <label>Activo:</label><br/>
                    <select name="activo">
                        <option value="true" ${localidad.activo ? 'selected' : ''}>Sí</option>
                        <option value="false" ${!localidad.activo ? 'selected' : ''}>No</option>
                    </select>
                </div>
            </c:if>
            
            <button type="submit">
                <c:choose>
                    <c:when test="${esEdicion}">Guardar cambios</c:when>
                    <c:otherwise>Crear</c:otherwise>
                </c:choose>
            </button>
            <a style="margin-left: 10px;" href="${pageContext.request.contextPath}/localidades?action=listar">
                Volver
            </a>
        </form>
    </body>
</html>