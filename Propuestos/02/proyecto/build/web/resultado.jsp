<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Resultado de Compra</title>
    <link rel="stylesheet" href="estilos.css">
</head>
<body>
    <h2>Resumen de la Compra</h2>
    <p><%= request.getAttribute("resumen") %></p>
    <a href="index.jsp">Volver</a>
</body>
</html>
