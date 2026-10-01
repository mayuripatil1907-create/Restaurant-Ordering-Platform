import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ReservationHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

            String data = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            String[] values = data.split("&");

            String userId = "";
            String tableId = "";
            String reservationDate = "";
            String reservationTime = "";
            String guests = "";

            for (String value : values) {

                String[] pair = value.split("=", 2);

                if (pair.length < 2) {
                    continue;
                }

                if (pair[0].equals("user_id")) {
                    userId = pair[1];
                }

                if (pair[0].equals("table_id")) {
                    tableId = pair[1];
                }

                if (pair[0].equals("reservation_date")) {
                    reservationDate = pair[1];
                }

                if (pair[0].equals("reservation_time")) {
                    reservationTime = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                }

                if (pair[0].equals("number_of_guests")) {
                    guests = pair[1];
                }
            }

            String sql =
                    "INSERT INTO Reservations " +
                    "(user_id, table_id, reservation_date, reservation_time, number_of_guests) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(sql)
            ) {

                ps.setInt(1, Integer.parseInt(userId));
                ps.setInt(2, Integer.parseInt(tableId));
                ps.setString(3, reservationDate);
                String formattedTime = reservationTime;

if (reservationTime.length() == 5) {
    formattedTime = reservationTime + ":00";
}

ps.setString(4, formattedTime);
                ps.setInt(5, Integer.parseInt(guests));

                ps.executeUpdate();

                sendResponse(
                        exchange,
                        "Reservation successful!"
                );

            } catch (Exception e) {

                e.printStackTrace();

                sendResponse(
                        exchange,
                        "Reservation failed: " + e.getMessage()
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