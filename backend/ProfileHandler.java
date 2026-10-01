import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfileHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {

            String email = exchange.getRequestURI()
                    .getQuery();

            if (email == null || !email.startsWith("email=")) {
                sendResponse(exchange, "Email required");
                return;
            }

            email = URLDecoder.decode(email.substring(6), StandardCharsets.UTF_8);


            String sql = "SELECT name, email, role, phone FROM Users WHERE email = ?";

            try (
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
            ) {

                ps.setString(1, email);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    String response =
                            "name=" + rs.getString("name") +
                            "&email=" + rs.getString("email") +
                            "&role=" + rs.getString("role") +
                            "&phone=" + rs.getString("phone");

                    sendResponse(exchange, response);

                } else {

                    sendResponse(exchange, "User not found");
                }

            } catch (Exception e) {

                e.printStackTrace();

                sendResponse(
                        exchange,
                        "Profile failed: " + e.getMessage()
                );
            }

        } else {

            sendResponse(exchange, "Only GET method is allowed.");
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

        exchange.sendResponseHeaders(200, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}