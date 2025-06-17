import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/api/productos")
public class TablaProductosServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        String filtroNombre = request.getParameter("nombre");
        String filtroCategoria = request.getParameter("categoria");

        try (Connection conn = Conexion.conectar()) {
            String sql = "SELECT * FROM productos WHERE 1=1";

            if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
                sql += " AND Nombre_Producto LIKE ?";
            }

            if (filtroCategoria != null && !filtroCategoria.trim().isEmpty()) {
                sql += " AND Categoria = ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);

            int paramIndex = 1;
            if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + filtroNombre + "%");
            }

            if (filtroCategoria != null && !filtroCategoria.trim().isEmpty()) {
                stmt.setString(paramIndex++, filtroCategoria);
            }

            ResultSet rs = stmt.executeQuery();
            imprimirResultados(rs, out);

        } catch (Exception e) {
            out.println("<tr><td colspan='10'>Error: " + e.getMessage() + "</td></tr>");
        }
    }

    private void imprimirResultados(ResultSet rs, PrintWriter out) throws SQLException {
        while (rs.next()) {
            out.println("<tr>");
            out.println("<td>" + rs.getInt("idProducto") + "</td>");
            out.println("<td>" + rs.getString("Nombre_Producto") + "</td>");
            out.println("<td>" + rs.getString("Categoria") + "</td>");
            out.println("<td>" + rs.getString("Marca_Producto") + "</td>");
            out.println("<td>" + rs.getInt("Cantidad_Producto") + "</td>");
            out.println("<td>$" + rs.getDouble("Precio_Producto") + "</td>");
            out.println("<td>" + rs.getString("Nombre_Proveedor") + "</td>");
            out.println("<td>" + rs.getDate("Fecha_Caducidad") + "</td>");
            out.println("<td>" + rs.getString("Descripcion_Producto") + "</td>");
            out.println("<td>" + rs.getInt("Stock_Producto") + "</td>");
            out.println("</tr>");
        }
    }
}