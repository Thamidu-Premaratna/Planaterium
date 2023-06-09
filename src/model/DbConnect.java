package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DbConnect {

//Variales for the class (to initiate a database connection)
    public static Connection connection; // Public access
    private static final String DATABASE = "planaterium_final";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";
//------------------------------------------------------------------------------    
//                     Create a new connection if it does not exists
//------------------------------------------------------------------------------

    public static Connection createConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DATABASE, USERNAME, PASSWORD);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
//------------------------------------------------------------------------------    
//                      Close connection if it exists
//------------------------------------------------------------------------------

    public static void closeConnection() {
        try {
            if (connection != null || !connection.isClosed()) {
                connection.close();
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
//------------------------------------------------------------------------------    
//                      Extra functions (not used) - using Statement
//------------------------------------------------------------------------------

    public static void iud(String query) { //Insert/Update/Delete using 'Statement'
        try {
            connection.createStatement().executeUpdate(query);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet search(String query) throws Exception { //Search using 'Statement'
        ResultSet rs1 = connection.createStatement().executeQuery(query);
        connection.close();
        return rs1;
    }

}
