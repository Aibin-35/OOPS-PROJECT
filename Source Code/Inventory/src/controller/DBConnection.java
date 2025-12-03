// src/controller/DBConnection.java
package controller;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL  =
        "jdbc:mysql://localhost:3306/inventory_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";     // XAMPP default
    private static final String PASS = "";         // XAMPP default is empty

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
