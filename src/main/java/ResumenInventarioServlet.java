import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/resumen")
public class ResumenInventarioServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = Conexion.conectar()) {
            JSONObject resumen = new JSONObject();

            // Productos con stock bajo (menos del 40% del stock máximo)
            JSONArray stockBajo = new JSONArray();
            String sqlStockBajo = "SELECT * FROM productos WHERE Cantidad_Producto < Stock_Producto * 0.4 ORDER BY (Cantidad_Producto / Stock_Producto) ASC LIMIT 3";
            try (PreparedStatement stmt = conn.prepareStatement(sqlStockBajo);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JSONObject producto = new JSONObject();
                    producto.put("idProducto", rs.getInt("idProducto"));
                    producto.put("Nombre_Producto", rs.getString("Nombre_Producto"));
                    producto.put("Cantidad_Producto", rs.getInt("Cantidad_Producto"));
                    producto.put("Stock_Producto", rs.getInt("Stock_Producto"));
                    stockBajo.put(producto);
                }
            }
            resumen.put("stockBajo", stockBajo);

            // Productos próximos a expirar (en los próximos 3 meses)
            JSONArray proximosExpirar = new JSONArray();
            String sqlExpirar = "SELECT * FROM productos WHERE Fecha_Caducidad BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL 3 MONTH) ORDER BY Fecha_Caducidad ASC LIMIT 3";
            try (PreparedStatement stmt = conn.prepareStatement(sqlExpirar);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JSONObject producto = new JSONObject();
                    producto.put("idProducto", rs.getInt("idProducto"));
                    producto.put("Nombre_Producto", rs.getString("Nombre_Producto"));
                    producto.put("Fecha_Caducidad", rs.getDate("Fecha_Caducidad").toString());
                    proximosExpirar.put(producto);
                }
            }
            resumen.put("proximosExpirar", proximosExpirar);

            // Resumen por categorías con colores definidos
            JSONObject resumenCategorias = new JSONObject();
            JSONArray categorias = new JSONArray();
            int total = 0;

            String sqlCategorias = "SELECT Categoria, COUNT(*) AS cantidad FROM productos GROUP BY Categoria";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCategorias);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JSONObject categoria = new JSONObject();
                    String nombreCategoria = rs.getString("Categoria");
                    categoria.put("categoria", nombreCategoria);
                    int cant = rs.getInt("cantidad");
                    categoria.put("cantidad", cant);

                    // Asignar colores según categoría
                    String color = getColorForCategory(nombreCategoria);
                    categoria.put("color", color);

                    categorias.put(categoria);
                    total += cant;
                }
            }

            resumenCategorias.put("categorias", categorias);
            resumenCategorias.put("total", total);
            resumen.put("resumenCategorias", resumenCategorias);

            out.print(resumen.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private String getColorForCategory(String categoria) {
        switch(categoria) {
            case "Medicamento": return "#067f7f";
            case "Alimento": return "#4CAF50";
            case "Accesorio": return "#2196F3";
            case "Insumo": return "#9C27B0";
            default: return "#cccccc";
        }
    }
}