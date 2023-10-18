package org.example;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DataTransfer {
    public DataTransfer() {

    }
    public void Start (){
        clearDB();
        try (
                Connection sourceConnection = SqlConnection.getSourceConnection();
                Connection destinationConnection = SqlConnection.getDestinationConnection();
        ) {
            System.out.println("Connected to both databases!");

            // Retrieve data from the source database
            ResultSet resultSet = fetchDataFromSourceDatabase(sourceConnection);

            // Process the results and insert into the destination database
            while (resultSet.next()) {
                String dataToTransfer1 = resultSet.getString("Id"); // Adjust with the appropriate column name
                String dataToTransfer2 = resultSet.getString("TnvedCode"); // Adjust with the appropriate column name
                String dataToTransfer3 = resultSet.getString("Name"); // Adjust with the appropriate column name
                String dataToTransfer4 = resultSet.getString("Barcode"); // Adjust with the appropriate column name
                insertDataIntoDestinationDatabase(destinationConnection, dataToTransfer1, dataToTransfer2, dataToTransfer3, dataToTransfer4);
            }

            //System.out.println("Data transfer completed!");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private ResultSet fetchDataFromSourceDatabase(Connection sourceConnection) throws SQLException {
        String query = "SELECT \"Id\",\"TnvedCode\", \"Name\", \"Barcode\" FROM \"TempProduct\" WHERE \"Status\" = '1' ORDER BY \"Barcode\""; // Adjust with the appropriate table name
        PreparedStatement preparedStatement = sourceConnection.prepareStatement(query);
        return preparedStatement.executeQuery();
    }

    private void insertDataIntoDestinationDatabase(Connection destinationConnection, String dataToTransfer1, String dataToTransfer2, String dataToTransfer3, String dataToTransfer4) throws SQLException {
        String insertQuery = "INSERT INTO tempproduct (id, tnved, name, barcode) VALUES (?, ?, ?, ?)"; // Adjust with the appropriate table and column names
        PreparedStatement preparedStatement = destinationConnection.prepareStatement(insertQuery);
        preparedStatement.setString(1, dataToTransfer1);
        preparedStatement.setString(2, dataToTransfer2);
        preparedStatement.setString(3, dataToTransfer3);
        preparedStatement.setString(4, dataToTransfer4);
        preparedStatement.executeUpdate();
    }

    private void clearDB() {
        try {
            Connection connection = SqlConnection.getDestinationConnection();
            String clearQuery = "DELETE FROM tempproduct";
            PreparedStatement preparedStatement = connection.prepareStatement(clearQuery);
            preparedStatement.executeUpdate();
            System.out.println("Data cleared in the destination table.");
        } catch (SQLException e) {
            System.err.println("Failed to clear data in the destination table. Error: " + e.getMessage());
        }
    }
    public Map<String, String> fetchTempProduct() {
        Map<String, String> pairMap = new HashMap<>();

        try (Connection connection = SqlConnection.getDestinationConnection();) {
            String query = "SELECT \"id\", barcode FROM \"tempproduct\" ORDER BY barcode";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String id = resultSet.getString("id");
                        String barcode = resultSet.getString("barcode");

                        if (barcode != null) {
                            pairMap.put(id, barcode);
                        } else break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pairMap;
    }
}
