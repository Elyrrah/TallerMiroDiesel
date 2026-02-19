<%-- 
    Document   : usuario_form
    Created on : 19 feb. 2026, 9:22:04 a. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>
        <c:choose>
            <c:when test="${empty usuario.idUsuario}">Nuevo Usuario</c:when>
            <c:otherwise>Editar Usuario</c:otherwise>
        </c:choose>
    </title>

    <style>
        label { display: block; margin-top: 10px; font-weight: bold; font-size: 14px; }
        input[type="text"],
        input[type="password"],
        select { padding: 6px; width: 300px; margin-top: 4px; }
        button { margin-top: 16px; padding: 8px 16px; }
    </style>
</head>
<body>

    <p>
        <a href="${pageContext.request.contextPath}/usuarios?action=listar">Volver al listado</a>
    </p>

    <h2>
        <c:choose>
            <c:when test="${empty usuario.idUsuario}">Nuevo Usuario</c:when>
            <c:otherwise>Editar Usuario</c:otherwise>
        </c:choose>
    </h2>

    <%-- Mensaje de error --%>
    <c:if test="${not empty error}">
        <div style="color:red; margin-bottom:10px;">${error}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/usuarios">
        <input type="hidden" name="action" value="guardar"/>
        <input type="hidden" name="idUsuario" value="${usuario.idUsuario}"/>

        <%-- Username: solo visible al crear, no al editar --%>
        <c:if test="${empty usuario.idUsuario}">
            <label>Username</label>
            <input type="text" name="username" value="${usuario.username}" autocomplete="off"/>
        </c:if>

        <label>Nombre</label>
        <input type="text" name="nombre" value="${usuario.nombre}"/>

        <label>Apellido</label>
        <input type="text" name="apellido" value="${usuario.apellido}"/>

        <label>Tipo de Documento</label>
        <select name="idTipoDocumento">
            <option value="">-- Seleccionar --</option>
            <c:forEach var="td" items="${tiposDocumento}">
                <option value="${td.idTipoDocumento}"
                    <c:if test="${td.idTipoDocumento == usuario.idTipoDocumento}">selected</c:if>>
                    ${td.nombre}
                </option>
            </c:forEach>
        </select>

        <label>Número de Documento</label>
        <input type="text" name="numeroDocumento" value="${usuario.numeroDocumento}"/>

        <label>Email</label>
        <input type="text" name="email" value="${usuario.email}"/>

        <label>Teléfono</label>
        <input type="text" name="telefono" value="${usuario.telefono}"/>

        <label>Rol</label>
        <select name="idRol">
            <option value="">-- Seleccionar --</option>
            <c:forEach var="r" items="${roles}">
                <option value="${r.idRol}"
                    <c:if test="${r.idRol == usuario.rol.idRol}">selected</c:if>>
                    ${r.nombre}
                </option>
            </c:forEach>
        </select>

        <%-- Contraseña: solo visible al crear --%>
        <c:if test="${empty usuario.idUsuario}">
            <label>Contraseña</label>
            <input type="password" name="password" autocomplete="off"/>
        </c:if>

        <%-- Activo: solo visible al editar --%>
        <c:if test="${not empty usuario.idUsuario}">
            <label>Estado</label>
            <select name="activo">
                <option value="true"  <c:if test="${usuario.activo}">selected</c:if>>ACTIVO</option>
                <option value="false" <c:if test="${!usuario.activo}">selected</c:if>>INACTIVO</option>
            </select>
        </c:if>

        <br/>
        <button type="submit">Guardar</button>
    </form>

</body>
</html>