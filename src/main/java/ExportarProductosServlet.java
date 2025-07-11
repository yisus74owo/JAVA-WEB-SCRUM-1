
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; // Asegúrate de que esta importación esté presente
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream; // Necesario para escribir el archivo Excel
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData; // Necesario para obtener los nombres de las columnas
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date; // Necesario para manejar fechas

// Importaciones de Apache POI
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Para archivos .xlsx

// Si tus clases 'Usuario' o 'Conexion' están en un paquete diferente al que declaraste arriba,
// deberás descomentar y ajustar las siguientes líneas:
// import com.veterinaria.modelo.Usuario; // Ejemplo, ajusta según tu estructura real
// import com.veterinaria.util.Conexion; // Ejemplo, ajusta según tu estructura real

@WebServlet("/api/productos/exportar")
public class ExportarProductosServlet extends HttpServlet {

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCORSHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setCORSHeaders(response);

        HttpSession session = request.getSession(false);

        // 1. Verificación de Autenticación y Rol (Admin)
        if (session == null || session.getAttribute("usuario") == null) {
            // CORRECCIÓN: HttpServletServlet a HttpServletResponse
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"No autenticado. Por favor, inicie sesión.\"}");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !usuario.getRol().equals("Administrador")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Acceso denegado. Solo administradores pueden exportar productos.\"}");
            return;
        }

        // 2. Configuración de Cabeceras para la Descarga de Archivo Excel
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Tipo MIME para .xlsx
        String fileName = "inventario_productos_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // 3. Generación del Archivo Excel usando Apache POI
        try (Workbook workbook = new XSSFWorkbook(); // Crea un nuevo libro de trabajo (.xlsx)
             OutputStream outputStream = response.getOutputStream(); // Obtiene el flujo de salida para escribir la respuesta
             Connection conn = Conexion.conectar(); // Usa tu método de conexión a DB
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM productos ORDER BY Nombre_Producto ASC"); // Prepara la consulta SQL
             ResultSet rs = stmt.executeQuery()) { // Ejecuta la consulta

            Sheet sheet = workbook.createSheet("Inventario Productos"); // Crea una nueva hoja en el libro

            // Estilo para el encabezado
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex()); // Texto blanco

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex()); // Fondo azul oscuro
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);

            // Estilo para celdas de datos con bordes
            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderTop(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);
            dataCellStyle.setWrapText(true); // Permite que el texto se ajuste en la celda

            // Estilo para fechas
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.cloneStyleFrom(dataCellStyle); // Copia los bordes
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

            // Crear fila de encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Categoría", "Marca", "Cantidad", "Precio", "Proveedor", "Caducidad", "Descripción", "Stock"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Llenar datos
            int rowNum = 1; // Empieza desde la segunda fila (la primera es para encabezados)
            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);

                // Columna ID
                Cell idCell = row.createCell(0);
                idCell.setCellValue(rs.getInt("idProducto"));
                idCell.setCellStyle(dataCellStyle);

                // Columna Nombre
                Cell nombreCell = row.createCell(1);
                nombreCell.setCellValue(rs.getString("Nombre_Producto"));
                nombreCell.setCellStyle(dataCellStyle);

                // Columna Categoría
                Cell categoriaCell = row.createCell(2);
                categoriaCell.setCellValue(rs.getString("Categoria"));
                categoriaCell.setCellStyle(dataCellStyle);

                // Columna Marca
                Cell marcaCell = row.createCell(3);
                marcaCell.setCellValue(rs.getString("Marca_Producto"));
                marcaCell.setCellStyle(dataCellStyle);

                // Columna Cantidad
                Cell cantidadCell = row.createCell(4);
                cantidadCell.setCellValue(rs.getInt("Cantidad_Producto"));
                cantidadCell.setCellStyle(dataCellStyle);

                // Columna Precio
                Cell precioCell = row.createCell(5);
                precioCell.setCellValue(rs.getDouble("Precio_Producto"));
                precioCell.setCellStyle(dataCellStyle);

                // Columna Proveedor
                Cell proveedorCell = row.createCell(6);
                proveedorCell.setCellValue(rs.getString("Nombre_Proveedor"));
                proveedorCell.setCellStyle(dataCellStyle);

                // Columna Fecha_Caducidad
                Cell caducidadCell = row.createCell(7);
                Date fechaCaducidad = rs.getDate("Fecha_Caducidad");
                if (fechaCaducidad != null) {
                    caducidadCell.setCellValue(fechaCaducidad);
                    caducidadCell.setCellStyle(dateCellStyle); // Aplica el estilo de fecha
                } else {
                    caducidadCell.setCellValue(""); // Deja vacío si es nulo
                    caducidadCell.setCellStyle(dataCellStyle);
                }

                // Columna Descripción
                Cell descripcionCell = row.createCell(8);
                descripcionCell.setCellValue(rs.getString("Descripcion_Producto"));
                descripcionCell.setCellStyle(dataCellStyle);

                // Columna Stock
                Cell stockCell = row.createCell(9);
                stockCell.setCellValue(rs.getInt("Stock_Producto"));
                stockCell.setCellStyle(dataCellStyle);
            }

            // Ajustar automáticamente el ancho de las columnas después de llenar los datos
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Opcional: A veces autoSizeColumn puede hacer las columnas muy estrechas.
                // Puedes añadir un poco de padding si lo deseas.
                // sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 500);
            }

            // Escribir el libro de trabajo en el flujo de salida de la respuesta
            workbook.write(outputStream);
            outputStream.flush();

        } catch (SQLException e) {
            System.err.println("Error de base de datos al exportar productos a Excel: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Error de base de datos al exportar a Excel: " + escapeJson(e.getMessage()) + "\"}");
        } catch (IOException e) {
            System.err.println("Error de I/O al exportar productos a Excel: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Error de I/O al exportar a Excel: " + escapeJson(e.getMessage()) + "\"}");
        } catch (Exception e) {
            System.err.println("Error inesperado al exportar productos a Excel: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Error interno del servidor al exportar: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    // Helper para escapar JSON, útil para mensajes de error
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "'").replace("\n", " ").replace("\r", " ");
    }
}