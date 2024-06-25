package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    // Thay đổi các thông số kết nối tương ứng với CSDL của bạn
    private static final String URL = "jdbc:mysql://localhost:3306/dbRapChieuPhim?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    // Phương thức để mở kết nối đến CSDL
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Tạo kết nối
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Phương thức để đóng kết nối đến CSDL
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
