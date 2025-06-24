import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.stream.Collectors;

@WebServlet(name = "CrearProductoServlet", urlPatterns = {"/api/productos/crear/*"})
public class CrearProductoServlet extends HttpServlet {

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCORSHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setCORSHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String requestBody = request.getReader().lines().collect(Collectors.joining());

            if (requestBody == null || requestBody.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Cuerpo de solicitud vacío\"}");
                return;
            }

            Producto producto = new Gson().fromJson(requestBody, Producto.class);

            if (validarProducto(producto)) {
                if (crearProducto(producto)) {
                    out.write("{\"success\":true,\"message\":\"Producto creado correctamente\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.write("{\"error\":\"No se pudo crear el producto\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Datos del producto inválidos o incompletos\"}");
            }

        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"JSON mal formado: " + escapeJson(e.getMessage()) + "\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error de base de datos: " + escapeJson(e.getMessage()) + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error inesperado: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private boolean validarProducto(Producto producto) {
        return producto.getNombreProducto() != null && !producto.getNombreProducto().trim().isEmpty()
                && producto.getCategoria() != null && !producto.getCategoria().trim().isEmpty()
                && producto.getMarcaProducto() != null && !producto.getMarcaProducto().trim().isEmpty()
                && producto.getCantidadProducto() >= 0
                && producto.getPrecioProducto() >= 0
                && producto.getStockProducto() >= 0;
    }

    private boolean crearProducto(Producto p) throws SQLException {
        String sql = "INSERT INTO productos (Nombre_Producto, Categoria, Marca_Producto, Cantidad_Producto, Precio_Producto, Nombre_Proveedor, Fecha_Caducidad, Descripcion_Producto, Stock_Producto) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getNombreProducto());
            stmt.setString(2, p.getCategoria());
            stmt.setString(3, p.getMarcaProducto());
            stmt.setInt(4, p.getCantidadProducto());
            stmt.setDouble(5, p.getPrecioProducto());
            stmt.setString(6, p.getNombreProveedor());

            if (p.getFechaCaducidad() != null) {
                stmt.setDate(7, new java.sql.Date(p.getFechaCaducidad().getTime()));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            stmt.setString(8, p.getDescripcionProducto());
            stmt.setInt(9, p.getStockProducto());

            return stmt.executeUpdate() > 0;
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "'").replace("\n", " ").replace("\r", " ");
    }

    public static class Producto {
        private String Nombre_Producto;
        private String Categoria;
        private String Marca_Producto;
        private int Cantidad_Producto;
        private double Precio_Producto;
        private String Nombre_Proveedor;
        private java.util.Date Fecha_Caducidad;
        private String Descripcion_Producto;
        private int Stock_Producto;

        public String getNombreProducto() { return Nombre_Producto; }
        public void setNombreProducto(String nombreProducto) { this.Nombre_Producto = nombreProducto; }
        public String getCategoria() { return Categoria; }
        public void setCategoria(String categoria) { this.Categoria = categoria; }
        public String getMarcaProducto() { return Marca_Producto; }
        public void setMarcaProducto(String marcaProducto) { this.Marca_Producto = marcaProducto; }
        public int getCantidadProducto() { return Cantidad_Producto; }
        public void setCantidadProducto(int cantidadProducto) { this.Cantidad_Producto = cantidadProducto; }
        public double getPrecioProducto() { return Precio_Producto; }
        public void setPrecioProducto(double precioProducto) { this.Precio_Producto = precioProducto; }
        public String getNombreProveedor() { return Nombre_Proveedor; }
        public void setNombreProveedor(String nombreProveedor) { this.Nombre_Proveedor = nombreProveedor; }
        public java.util.Date getFechaCaducidad() { return Fecha_Caducidad; }
        public void setFechaCaducidad(java.util.Date fechaCaducidad) { this.Fecha_Caducidad = fechaCaducidad; }
        public String getDescripcionProducto() { return Descripcion_Producto; }
        public void setDescripcionProducto(String descripcionProducto) { this.Descripcion_Producto = descripcionProducto; }
        public int getStockProducto() { return Stock_Producto; }
        public void setStockProducto(int stockProducto) { this.Stock_Producto = stockProducto; }
    }
}

