<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Error</title>
    <link rel="stylesheet" href="estilos.css">
</head>
<body>
    <h2>¡Ha ocurrido un error!</h2>

    <%
        if (exception != null) {
            out.println("<p><strong>Excepción:</strong> " + exception.getClass().getName() + "</p>");
            out.println("<p><strong>Mensaje:</strong> " + exception.getMessage() + "</p>");
        }

        String[] mensajes = (String[]) request.getAttribute("mensaje");
        if (mensajes != null) {
            out.println("<ul>");
            for (String msg : mensajes) {
                out.println("<li>" + msg + "</li>");
            }
            out.println("</ul>");
        }
    %>

    <a href="index.jsp">Volver al inicio</a>
</body>
</html>
