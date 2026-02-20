<%-- 
    Document   : acceso-denegado
    Created on : 20 feb. 2026, 10:27:15 a. m.
    Author     : elyrr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Acceso Denegado</title>

    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background-color: #f0f0f0;
            font-family: sans-serif;
        }

        .caja {
            background: #fff;
            padding: 32px 40px;
            border: 1px solid #ccc;
            width: 380px;
            text-align: center;
        }

        .caja h2 {
            margin: 0 0 16px 0;
            color: #c0392b;
        }

        .caja p {
            margin: 0 0 24px 0;
            color: #555;
        }

        .caja a {
            padding: 8px 20px;
            background-color: #333;
            color: #fff;
            text-decoration: none;
        }

        .caja a:hover {
            background-color: #555;
        }
    </style>
</head>
<body>

    <div class="caja">
        <h2>Acceso Denegado</h2>
        <p>
            No tenés permisos para acceder a esta sección.
            Contactá al administrador si creés que es un error.
        </p>
        <a href="${pageContext.request.contextPath}/">Volver al inicio</a>
    </div>

</body>
</html>