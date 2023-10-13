package org.example;

import java.sql.*;

public class ExternalDB {
    public ExternalDB() {
        Connection connection = null;
        try {
            // Register the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            connection = SqlConnection.getSourceConnection();

            if (connection != null) {
                System.out.println("Connected to the external database!");
                // Use the connection for database operations
                // ...

                // Close the connection
                connection.close();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Could not find the JDBC driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing the connection: " + e.getMessage());
            }
        }
    }
}
