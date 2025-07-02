import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/api/productos/*")
public class EliminarProductoServlet extends HttpServlet {
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        
        // Verificar autenticación y rol
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"No autenticado\"}");
            return;
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (!"Administrador".equals(usuario.getRol())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"Requiere rol de Administrador\"}");
            return;
        }

        // Extraer ID del producto
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"ID no proporcionado\"}");
            return;
        }

        String[] splits = pathInfo.split("/");
        if (splits.length != 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Formato de URL inválido\"}");
            return;
        }

        try {
            int idProducto = Integer.parseInt(splits[1]);
            
            try (Connection conn = Conexion.conectar()) {
                String sql = "DELETE FROM productos WHERE idProducto = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idProducto);
                    int filasAfectadas = ps.executeUpdate();
                    
                    if (filasAfectadas > 0) {
                        response.getWriter().write("{\"success\":true, \"message\":\"Producto eliminado\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\":\"Producto no encontrado\"}");
                    }
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"ID inválido\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error en la base de datos\"}");
        }
    }
}