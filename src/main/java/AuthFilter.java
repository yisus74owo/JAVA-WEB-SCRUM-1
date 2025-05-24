import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        String loginURI = request.getContextPath() + "/login.html";
        String loginServlet = request.getContextPath() + "/LoginServlet";

        boolean loggedIn = session != null && session.getAttribute("usuario") != null;
        boolean loginRequest = request.getRequestURI().equals(loginURI) ||
                request.getRequestURI().equals(loginServlet);

        if (loggedIn || loginRequest || request.getRequestURI().endsWith(".css") ||
                request.getRequestURI().endsWith(".js") || request.getRequestURI().endsWith(".png")) {
            chain.doFilter(request, response);
        } else {
            response.sendRedirect(loginURI);
        }
    }

    public void init(FilterConfig fConfig) throws ServletException {}
    public void destroy() {}
}