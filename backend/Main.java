import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) {

        try {

            // Test database connection
            if (DatabaseConnection.getConnection() == null) {
                System.out.println("Database connection failed!");
                return;
            }

            HttpServer server =
                    HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/register", new RegisterHandler());

            server.createContext("/login", new LoginHandler());

            server.createContext("/profile", new ProfileHandler());

            server.createContext("/order",new OrderHandler());

            server.createContext("/order-history", new OrderHistoryHandler());

            server.createContext("/reservation", new ReservationHandler());

            server.createContext("/reservation-history", new ReservationHistoryHandler());

            server.start();

            System.out.println("Restaurant Server Started!");
            System.out.println("Register API: http://localhost:8080/register");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}