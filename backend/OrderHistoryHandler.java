import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderHistoryHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, "Only GET method is allowed.");
            return;
        }

        String query = exchange.getRequestURI().getQuery();

        if (query == null || !query.startsWith("user_id=")) {
            sendResponse(exchange, "User ID required");
            return;
        }

        String userId = query.substring(8);

        String sql =
                "SELECT order_id, order_date, total_amount, order_type, status " +
                "FROM Orders WHERE user_id = ? ORDER BY order_date DESC";

        try (
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, Integer.parseInt(userId));

            ResultSet rs = ps.executeQuery();

            StringBuilder response = new StringBuilder();

            while (rs.next()) {

                response.append("order_id=")
                        .append(rs.getInt("order_id"))
                        .append("&order_date=")
                        .append(rs.getString("order_date"))
                        .append("&total_amount=")
                        .append(rs.getDouble("total_amount"))
                        .append("&order_type=")
                        .append(rs.getString("order_type"))
                        .append("&status=")
                        .append(rs.getString("status"))
                        .append("\n");
            }

            if (response.length() == 0) {
                sendResponse(exchange, "No orders found");
            } else {
                sendResponse(exchange, response.toString());
            }

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Order history failed: " + e.getMessage()
            );
        }
    }

    private void sendResponse(
            HttpExchange exchange,
            String response) throws IOException {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );

        byte[] bytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                200,
                bytes.length
        );

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}