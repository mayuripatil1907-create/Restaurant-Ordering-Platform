import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class OrderHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

            String data = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            String[] values = data.split("&");

            String userId = "";
            String totalAmount = "";

            for (String value : values) {

                String[] pair = value.split("=", 2);

                if (pair.length < 2) {
                    continue;
                }

                if (pair[0].equals("user_id")) {
                    userId = pair[1];
                }

                if (pair[0].equals("total_amount")) {
                    totalAmount = pair[1];
                }
            }

            String sql =
                    "INSERT INTO Orders (user_id, total_amount, order_type) " +
                    "VALUES (?, ?, 'Online')";

            try (
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
            ) {

                ps.setInt(1, Integer.parseInt(userId));
                ps.setDouble(2, Double.parseDouble(totalAmount));

                ps.executeUpdate();

                sendResponse(
                        exchange,
                        "Order placed successfully!"
                );

            } catch (Exception e) {

                e.printStackTrace();

                sendResponse(
                        exchange,
                        "Order failed: " + e.getMessage()
                );
            }

        } else {

            sendResponse(
                    exchange,
                    "Only POST method is allowed."
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