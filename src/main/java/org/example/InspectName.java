package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InspectName {
    public void Inspect(){
        Map<String, String> map = fetchTempProductName();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!startsWithLetter(entry.getValue()))
                UpdateComment(entry.getKey(),"Наименование должно начинаться с буквы");
            else if (wordCount(entry.getValue(),2)) {
                UpdateComment(entry.getKey(), "Укажите наименование подробнее согласно руководству.");
            } else if (hasConsecutiveRepeatingIntonationSymbols(entry.getValue())){
                UpdateComment(entry.getKey(),"Символы интонации не должны повторяться более одного раза");
                System.out.println(entry.getValue());
            }
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
            return;
        }

        // Combine existing comment and new comment with a space
        String updatedComment;
        if (existingComment == null) updatedComment = comm; else  updatedComment = existingComment + " " + comm;

        String updateQuery = "UPDATE tempproduct SET comment = ? WHERE id = ?";

        try (Connection connection = SqlConnection.getDestinationConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, updatedComment);
            preparedStatement.setString(2, key);
            int rowsAffected = preparedStatement.executeUpdate();

//            if (rowsAffected > 0) {
//                System.out.println("Comment updated successfully for key: " + key);
//            } else {
//                System.out.println("No rows were updated for key: " + key);
//            }
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
    public boolean wordCount(String input, int n) {
        if (input != null && !input.isEmpty()) {
            input = removeLastNonLetterSymbol(input);
            String[] words = input.split(" ");
            if (words.length > n ) return false;
            for (String word : words) {
                if (!word.matches("^[a-zA-Zа-яА-Я]+$")) {

                    return false;
                }
            }
        }
        return true;
    }
    public boolean hasConsecutiveRepeatingIntonationSymbols(String input) {
        if (input != null && !input.isEmpty()) {
            String intonationSymbols = "?!.,:;@#$^&*_-+={}[]<>/\\'\"\\";
            for (int i = 0; i < input.length() - 1; i++) {
                char currentChar = input.charAt(i);
                char nextChar = input.charAt(i + 1);

                if (intonationSymbols.indexOf(currentChar) != -1 &&
                        intonationSymbols.indexOf(nextChar) != -1 &&
                        currentChar == nextChar) {
                    return true;
                }
            }
        }
        return false;
    }

    private String removeLastNonLetterSymbol(String word) {
        // Получаем последний символ
        char lastChar = word.charAt(word.length() - 1);

        // Проверяем, является ли последний символ буквой (кириллицей или латиницей)
        if (Character.isLetter(lastChar)) {
            return word;
        } else {
            // Убираем последний символ
            return word.substring(0, word.length() - 1);
        }
    }

}
