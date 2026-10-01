import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReservationHistoryHandler implements HttpHandler {

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
                "SELECT r.reservation_id, r.reservation_date, " +
                "r.reservation_time, r.number_of_guests, r.status, " +
                "t.table_number " +
                "FROM Reservations r " +
                "JOIN Restaurant_Tables t ON r.table_id = t.table_id " +
                "WHERE r.user_id = ? " +
                "ORDER BY r.reservation_date DESC, r.reservation_time DESC";

        try (
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, Integer.parseInt(userId));

            ResultSet rs = ps.executeQuery();

            StringBuilder response = new StringBuilder();

            while (rs.next()) {

                response.append("reservation_id=")
                        .append(rs.getInt("reservation_id"))
                        .append("&reservation_date=")
                        .append(rs.getString("reservation_date"))
                        .append("&reservation_time=")
                        .append(rs.getString("reservation_time"))
                        .append("&number_of_guests=")
                        .append(rs.getInt("number_of_guests"))
                        .append("&status=")
                        .append(rs.getString("status"))
                        .append("&table_number=")
                        .append(rs.getInt("table_number"))
                        .append("\n");
            }

            if (response.length() == 0) {

                sendResponse(exchange, "No reservations found");

            } else {

                sendResponse(exchange, response.toString());
            }

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Reservation history failed: " + e.getMessage()
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