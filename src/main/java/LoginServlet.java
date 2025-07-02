import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        try (Connection conn = Conexion.conectar()) {
            String sql = "SELECT u.idUsuario, u.Nombre_usuario, u.Correo, u.Contraseña, r.Nombre_rol " +
                         "FROM usuarios u JOIN roles r ON u.Rol_idRol = r.idRol " +
                         "WHERE u.Correo = ? AND u.Contraseña = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setEmail(rs.getString("Correo"));
                usuario.setNombre(rs.getString("Nombre_usuario"));
                usuario.setRol(rs.getString("Nombre_rol"));

                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuario);
                session.setMaxInactiveInterval(30 * 60);

                response.sendRedirect("inventorymodule.html");
            } else {
                response.sendRedirect("login.html?error=1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("login.html?error=2");
        }
    }
}