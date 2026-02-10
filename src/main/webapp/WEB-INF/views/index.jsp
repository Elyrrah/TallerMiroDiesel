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

        <a href="${pageContext.request.contextPath}/paises?action=list">Países</a> <br>
        <a href="${pageContext.request.contextPath}/departamentos?action=list">Departamentos</a><br>
        <a href="${pageContext.request.contextPath}/distritos?action=list">Distritos</a><br>
        <a href="${pageContext.request.contextPath}/localidades?action=list">Localidades</a><br>
        <a href="${pageContext.request.contextPath}/marcas?action=list">Marcas</a><br>
        <a href="${pageContext.request.contextPath}/modelos?action=list">Modelos</a><br>
        <a href="${pageContext.request.contextPath}/tipos-documento?action=list">Tipos de documento</a><br>
        <a href="${pageContext.request.contextPath}/servicios?action=list">Servicios</a><br>
        <a href="${pageContext.request.contextPath}/clientes?action=list">Clientes</a><br>

    </body>
</html>
