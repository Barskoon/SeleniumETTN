package org.example;
import java.sql.*;

public class ReadDataFromPostgres {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Goods";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            // Register the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            if (connection != null) {
                System.out.println("Connected to the database!");

                // Create a statement
                statement = connection.createStatement();

                // Prepare a statement for inserting data with named parameters
                String insertQuery = "INSERT INTO tempproduct (id, name, barcode) VALUES (?, ?, ?)";
                preparedStatement = connection.prepareStatement(insertQuery);

                // Set the values for the named parameters
                preparedStatement.setString(1, "sdfdsfsdfsdfsd");
                preparedStatement.setString(2, "snikers");
                preparedStatement.setString(3, "675465465465");

                // Execute the insert statement
                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("Data successfully inserted!");
                } else {
                    System.out.println("Failed to insert data.");
                }

                // Execute a query to retrieve data
                String query = "SELECT * FROM tempproduct";  // Replace "your_table" with your actual table name
                resultSet = statement.executeQuery(query);

                // Process the results
                while (resultSet.next()) {
                    String id = resultSet.getString("id");  // Assuming 'id' is a column in your table
                    String name = resultSet.getString("name");  // Assuming 'name' is a column in your table
                    // ... (retrieve other columns as needed)

                    System.out.println("ID: " + id + ", Name: " + name);  // Print the retrieved data
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Could not find the JDBC driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
