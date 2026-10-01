import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

            String data = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            String[] values = data.split("&");

            String name = "";
            String email = "";
            String password = "";

            for (String value : values) {

                String[] pair = value.split("=", 2);

                if (pair.length < 2) {
                    continue;
                }

                if (pair[0].equals("name")) {
                    name = pair[1];
                }

                if (pair[0].equals("email")) {
                    email = pair[1];
                }

                if (pair[0].equals("password")) {
                    password = pair[1];
                }
            }

            String sql =
                    "INSERT INTO Users (name, email, password) VALUES (?, ?, ?)";

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, password);

                ps.executeUpdate();

                sendResponse(exchange, "Registration successful!");

            } catch (Exception e) {

                e.printStackTrace();

                sendResponse(exchange, "Registration failed: " + e.getMessage());
            }

        } else {

            sendResponse(exchange, "Only POST method is allowed.");
        }
    }

    private void sendResponse(
            HttpExchange exchange,
            String response) throws IOException {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
        );

        exchange.sendResponseHeaders(
                200,
                response.getBytes(StandardCharsets.UTF_8).length
        );

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}