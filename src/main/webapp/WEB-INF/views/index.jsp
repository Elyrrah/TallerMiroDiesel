<%-- 
    Document   : index
    Created on : 14 ene. 2026, 2:49:08 p. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Taller Miro Diesel</title>
    </head>
    <body>
        <h1>Taller Miro Diesel</h1>
        <p>Aplicación en desarrollo.</p>

        <a href="${pageContext.request.contextPath}/paises?accion=listar">Países</a> <br>
        <a href="${pageContext.request.contextPath}/departamentos?accion=listar">Departamentos</a><br>
        <a href="${pageContext.request.contextPath}/ciudades?accion=listar">Ciudades</a><br>
        <a href="${pageContext.request.contextPath}/distritos?accion=listar">Distritos</a><br>
        <a href="${pageContext.request.contextPath}/marcas?accion=listar">Marcas</a><br>
        <a href="${pageContext.request.contextPath}/modelos?accion=listar">Modelos</a><br>
        <a href="${pageContext.request.contextPath}/tipos-documento?accion=listar">Tipos de documento</a><br>
        <a href="${pageContext.request.contextPath}/servicios?accion=listar">Servicios</a><br>

    </body>
</html>
