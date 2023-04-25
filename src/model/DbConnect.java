package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DbConnect {

    public static Connection connection;
    private static final String DATABASE = "planaterium";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    public static Connection createConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DATABASE, USERNAME, PASSWORD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void iud(String query) {
        try {
            connection.createStatement().executeUpdate(query);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet search(String query) throws Exception {
        ResultSet rs1 = connection.createStatement().executeQuery(query);
        connection.close();
        return rs1;
    }

}
