<%-- 
    Document   : usuario_cambiar_password
    Created on : 19 feb. 2026, 9:23:24 a. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cambiar Contraseña</title>

    <style>
        label { display: block; margin-top: 10px; font-weight: bold; font-size: 14px; }
        input[type="password"] { padding: 6px; width: 300px; margin-top: 4px; }
        button { margin-top: 16px; padding: 8px 16px; }
    </style>
</head>
<body>

    <p>
        <a href="${pageContext.request.contextPath}/usuarios?action=listar">Volver al listado</a>
    </p>

    <h2>Cambiar Contraseña — ${usuario.nombre} ${usuario.apellido}</h2>

    <%-- Mensaje de error --%>
    <c:if test="${not empty error}">
        <div style="color:red; margin-bottom:10px;">${error}</div>
    </c:if>

    <%-- Mensaje de éxito --%>
    <c:if test="${not empty exito}">
        <div style="color:green; margin-bottom:10px;">${exito}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/usuarios">
        <input type="hidden" name="action" value="cambiarPassword"/>
        <input type="hidden" name="idUsuario" value="${usuario.idUsuario}"/>

        <label>Contraseña actual</label>
        <input type="password" name="passwordActual" autocomplete="off"/>

        <label>Nueva contraseña</label>
        <input type="password" name="passwordNueva" autocomplete="off"/>

        <label>Confirmar nueva contraseña</label>
        <input type="password" name="passwordConfirmar" autocomplete="off"/>

        <br/>
        <button type="submit">Cambiar contraseña</button>
    </form>

</body>
</html>