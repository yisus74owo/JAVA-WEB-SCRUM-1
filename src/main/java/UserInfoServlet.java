import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/api/user-info")
public class UserInfoServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"No autenticado\"}");
            return;
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String json = String.format(
            "{\"nombre\":\"%s\", \"email\":\"%s\", \"rol\":\"%s\"}",
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getRol()
        );
        
        response.getWriter().write(json);
    }
}