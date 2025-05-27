package compra.productos;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "compraProductos", urlPatterns = {"/compraProductos"})
public class compraProductos extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("clave");

        ArrayList<String> mensajes = new ArrayList<>();

        if (!"admin".equals(usuario) || !"1234".equals(clave)) {
            mensajes.add("Usuario o contraseña incorrectos.");
            request.setAttribute("mensaje", mensajes.toArray(new String[0]));
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        try {
            int pan = Integer.parseInt(request.getParameter("pan"));
            int queso = Integer.parseInt(request.getParameter("queso"));
            int platanos = Integer.parseInt(request.getParameter("platanos"));
            int toronjas = Integer.parseInt(request.getParameter("toronjas"));

            if (pan < 1 || queso < 1 || platanos < 1 || toronjas < 1) {
                throw new IllegalArgumentException("Debe ingresar cantidades positivas.");
            }

            double total = 0;
            StringBuilder resumen = new StringBuilder();

            total += pan * 0.5;
            resumen.append("Pan: ").append(pan)
                   .append(" unidades --> SubTotal: ")
                   .append(String.format("%.2f", pan * 0.5)).append("<br>");

            total += queso * 2.5;
            resumen.append("Queso: ").append(queso)
                   .append(" unidades --> SubTotal: ")
                   .append(String.format("%.2f", queso * 2.5)).append("<br>");

            total += platanos * 0.4;
            resumen.append("Plátanos: ").append(platanos)
                   .append(" unidades --> SubTotal: ")
                   .append(String.format("%.2f", platanos * 0.4)).append("<br>");

            total += toronjas * 0.4;
            resumen.append("Toronjas: ").append(toronjas)
                   .append(" unidades --> SubTotal: ")
                   .append(String.format("%.2f", toronjas * 0.4)).append("<br>");

            resumen.append("<br><strong>Total: S/. ")
                   .append(String.format("%.2f", total)).append("</strong>");

            request.setAttribute("resumen", resumen.toString());
            request.getRequestDispatcher("resultado.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("mensaje", new String[]{"Error al procesar la compra: " + e.getMessage()});
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}
