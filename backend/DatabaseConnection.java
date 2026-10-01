import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/restaurant_db";

    private static final String USER = "root";

    private static final String PASSWORD = "My_Password";

    public static Connection getConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection =
                    DriverManager.getConnection(URL, USER, PASSWORD);

            System.out.println("Database connected successfully!");

            return connection;

        } catch (ClassNotFoundException e) {

            System.out.println("MySQL JDBC Driver not found!");

        } catch (SQLException e) {

            System.out.println("Database connection failed!");
            System.out.println(e.getMessage());

        }

        return null;
    }
}