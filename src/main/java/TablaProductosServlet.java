import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/tabla-productos")
public class TablaProductosServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = Conexion.conectar()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM productos");

            out.println("<table border='1' style='width:100%; border-collapse: collapse; text-align: center;'>");
            out.println("<tr><th>ID</th><th>Nombre</th><th>Precio</th><th>Cantidad</th></tr>");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("idProducto") + "</td>");
                out.println("<td>" + rs.getString("Nombre_Producto") + "</td>");
                out.println("<td>$" + rs.getDouble("Precio_Producto") + "</td>");
                out.println("<td>" + rs.getInt("Cantidad_Producto") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error al cargar productos: " + e.getMessage() + "</p>");
        }

    }
}


