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

        String filtroNombre = request.getParameter("nombre");

        try (Connection conn = Conexion.conectar()) {
            PreparedStatement stmt;
            if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
                stmt = conn.prepareStatement("SELECT * FROM productos WHERE Nombre_Producto LIKE ?");
                stmt.setString(1, "%" + filtroNombre + "%");
            } else {
                stmt = conn.prepareStatement("SELECT * FROM productos");
            }

            ResultSet rs = stmt.executeQuery();

            out.println("<form method='get'>");
            out.println("Filtrar por nombre: <input type='text' name='nombre' value='" + (filtroNombre != null ? filtroNombre : "") + "' />");
            out.println("<input type='submit' value='Buscar' />");
            out.println("</form><br>");

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
