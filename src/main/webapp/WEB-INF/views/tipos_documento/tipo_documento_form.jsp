<%-- 
    Document   : tipos_documento_form
    Created on : 27 ene. 2026, 9:54:35 a. m.
    Author     : elyrr
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario Tipo de Documento</title>
</head>
<body>

<c:if test="${not empty error}">
    <div style="color:red; margin-bottom:10px;">
        ${error}
    </div>
</c:if>

<c:set var="esEdicion" value="${not empty tipoDocumento.idTipoDocumento}" />

<h2>
    <c:choose>
        <c:when test="${esEdicion}">Editar Tipo de Documento</c:when>
        <c:otherwise>Nuevo Tipo de Documento</c:otherwise>
    </c:choose>
</h2>

<form method="post" action="${pageContext.request.contextPath}/tipos-documento">

    <input type="hidden" name="accion" value="guardar" />

    <c:if test="${esEdicion}">
        <input type="hidden"
               name="idTipoDocumento"
               value="${tipoDocumento.idTipoDocumento}" />
    </c:if>

    <div>
        <label>Nombre</label><br/>
        <input type="text"
               name="nombre"
               value="${tipoDocumento.nombre}"
               required />
    </div>

    <div>
        <label>Código</label><br/>
        <input type="text"
               name="codigo"
               value="${tipoDocumento.codigo}"
               required />
    </div>

    <div>
        <label>Aplica a</label><br/>
        <select name="aplicaA" required>
            <option value="">-- Seleccionar --</option>
            <c:forEach var="opcion" items="${aplicaAOptions}">
                <option value="${opcion}"
                    <c:if test="${tipoDocumento.aplicaA == opcion}">
                        selected
                    </c:if>
                >
                    ${opcion}
                </option>
            </c:forEach>
        </select>
    </div>

    <c:if test="${esEdicion}">
        <div>
            <label>Activo</label><br/>
            <select name="activo">
                <option value="true" ${tipoDocumento.activo ? 'selected' : ''}>Sí</option>
                <option value="false" ${!tipoDocumento.activo ? 'selected' : ''}>No</option>
            </select>
        </div>
    </c:if>

    <br/>

    <button type="submit">Guardar</button>
    <a href="${pageContext.request.contextPath}/tipos-documento?accion=listar">
        Cancelar
    </a>
</form>

</body>
</html>
