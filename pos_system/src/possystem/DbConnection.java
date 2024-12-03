/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package possystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author PCS
 */
public class DbConnection {

    private static final String URL = "jdbc:mysql://localhost:3308/pos"; // Use your DB name here
    private static final String USERNAME = "root"; // Use your DB username
    private static final String PASSWORD = "1234"; // Use your DB password

    public static Connection getConnection() {
        try {
            // Load the MySQL JDBC driver (if not using automatic loading)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
