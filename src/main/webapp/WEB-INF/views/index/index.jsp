<%-- 
    Document   : index
    Created on : 14 ene. 2026, 2:49:08 p. m.
    Author     : elyrr
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Taller Miro Diesel</title>
</head>
<body>

    <h1>Taller Miro Diesel</h1>

    <%-- Muestra el usuario logueado y el botón de cerrar sesión --%>
    <p>
        Bienvenido, <strong>${usuarioSesion.nombre} ${usuarioSesion.apellido}</strong>
        (${usuarioSesion.nombreRol})
        &nbsp;|&nbsp;
        <form method="post" action="${pageContext.request.contextPath}/login" style="display:inline;">
            <input type="hidden" name="action" value="logout"/>
            <button type="submit">Cerrar sesión</button>
        </form>
    </p>

    <hr/>

    <h3>Gestión Geográfica</h3>
    <a href="${pageContext.request.contextPath}/paises?action=listar">Países</a> <br>
    <a href="${pageContext.request.contextPath}/departamentos?action=listar">Departamentos</a><br>
    <a href="${pageContext.request.contextPath}/distritos?action=listar">Distritos</a><br>
    <a href="${pageContext.request.contextPath}/localidades?action=listar">Localidades</a><br>

    <h3>Catálogos</h3>
    <a href="${pageContext.request.contextPath}/marcas?action=listar">Marcas</a><br>
    <a href="${pageContext.request.contextPath}/modelos?action=listar">Modelos</a><br>
    <a href="${pageContext.request.contextPath}/tipos-documento?action=listar">Tipos de Documento</a><br>
    <a href="${pageContext.request.contextPath}/servicios?action=listar">Servicios</a><br>

    <h3>Clientes</h3>
    <a href="${pageContext.request.contextPath}/clientes?action=listar">Clientes</a><br>

    <h3>Sistema</h3>
    <a href="${pageContext.request.contextPath}/usuarios?action=listar">Usuarios</a><br>

</body>
</html>