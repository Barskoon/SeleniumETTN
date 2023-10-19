package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InspectName {
    public static void main(String[] args) {
        InspectName ins = new InspectName();
        ins.Inspect();
    }
    public void Inspect(){
        Map<String, String> map = fetchTempProductName();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!startsWithLetter(entry.getValue()))
                UpdateComment(entry.getKey(),"Наименование должно начинаться с буквы");
            else if (containsOnlyLetters(entry.getValue()))
                UpdateComment(entry.getKey(),"Укажите наименование подробнее согласно руководству.");
        }
    }
    private void UpdateComment(String key, String comm) {
        String selectQuery = "SELECT comment FROM tempproduct WHERE id = ?";
        String existingComment = "";

        try (Connection connection = SqlConnection.getDestinationConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            selectStatement.setString(1, key);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                existingComment = resultSet.getString("comment");
            }
        } catch (SQLException e) {
            System.err.println("SQL exception occurred while selecting existing comment: " + e.getMessage());
            return;
        }

        // Combine existing comment and new comment with a space
        String updatedComment = existingComment + " " + comm;

        String updateQuery = "UPDATE tempproduct SET comment = ? WHERE id = ?";

        try (Connection connection = SqlConnection.getDestinationConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, updatedComment);
            preparedStatement.setString(2, key);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Comment updated successfully for key: " + key);
            } else {
                System.out.println("No rows were updated for key: " + key);
            }
        } catch (SQLException e) {
            System.err.println("SQL exception occurred while updating comment: " + e.getMessage());
        }
    }

    public Map<String, String> fetchTempProductName() {
        Map<String, String> pairMap = new HashMap<>();
        try (Connection connection = SqlConnection.getDestinationConnection();) {
            String query = "SELECT \"id\", name FROM \"tempproduct\" ORDER BY barcode";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String id = resultSet.getString("id");
                        String name = resultSet.getString("name");
                        pairMap.put(id, name);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pairMap;
    }
    public boolean startsWithLetter(String input) {
        if (input != null && !input.isEmpty()) {
            char firstChar = input.charAt(0);
            return Character.isLetter(firstChar);
        }
        return false; // Возвращаем false, если строка пуста или null
    }
    public boolean containsOnlyLetters(String input) {
        if (input != null && !input.isEmpty()) {
            boolean foundSpace = false;
            for (char c : input.toCharArray()) {
                if (!Character.isLetter(c)) {
                    if (c == ' ') {
                        if (foundSpace) {
                            return false;
                        } else {
                            foundSpace = true;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return foundSpace;
        }
        return false;
    }
}
