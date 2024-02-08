package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection {
    private static final String DB_URL1 = "jdbc:postgresql://10.49.17.250:5001/Ettn";
    private static final String DB_URL2 = "jdbc:postgresql://localhost:5432/goods";
    private static final String USER1 = "imukanov";
    private static final String USER2 = "postgres";
    private static final String PASSWORD1 = "Boh1ooma";
    private static final String PASSWORD2 = "postgres";

    public static Connection getSourceConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL1, USER1, PASSWORD1);
    }

    public static Connection getDestinationConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL2, USER2, PASSWORD2);
    }
}