<%@ page contentType="text/html" pageEncoding="UTF-8" errorPage="error.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Compras</title>
    <link rel="stylesheet" href="estilos.css">
</head>
<body>
    <h2>¡Bienvenido a la compra de productos!</h2>
    <form action="compraProductos" method="post">
        Usuario: <input type="text" name="usuario"><br>
        Contraseña: <input type="password" name="clave"><br>
        Cantidad Pan: <input type="number" name="pan"><br>
        Cantidad Queso: <input type="number" name="queso"><br>
        Cantidad Plátanos: <input type="number" name="platanos"><br>
        Cantidad Toronjas: <input type="number" name="toronjas"><br>
        <input type="submit" value="Comprar">
    </form>
</body>
</html>
