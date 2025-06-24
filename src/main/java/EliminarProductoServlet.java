import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/api/productos/*")
public class EliminarProductoServlet extends HttpServlet {
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Extraer ID de la URL (ej: /api/productos/5)
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\": \"ID de producto no proporcionado\"}");
                return;
            }

            String[] splits = pathInfo.split("/");
            if (splits.length != 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\": \"Formato de URL inválido\"}");
                return;
            }

            int idProducto = Integer.parseInt(splits[1]);

            // Eliminar producto
            try (Connection conexion = Conexion.conectar()) {
                String sql = "DELETE FROM productos WHERE idProducto = ?";
                try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                    ps.setInt(1, idProducto);
                    int filasAfectadas = ps.executeUpdate();

                    if (filasAfectadas > 0) {
                        out.write("{\"success\": true, \"message\": \"Producto eliminado correctamente\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.write("{\"success\": false, \"error\": \"Producto no encontrado\"}");
                    }
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"success\": false, \"error\": \"ID de producto inválido\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"success\": false, \"error\": \"Error en la base de datos: " + e.getMessage() + "\"}");
        }
    }
}