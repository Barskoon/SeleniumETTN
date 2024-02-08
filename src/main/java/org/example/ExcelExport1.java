package org.example;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
public class ExcelExport1 {
    private int iteration = 1;
    public static void main(String[] args) {
        ExcelExport1 ex = new ExcelExport1();
        ex.start();
    }

    public void start() {
        List<String> productIds = fetchProductIds();

        for (String productId : productIds) {
            excelEx(productId);
        }
    }

    private List<String> fetchProductIds() {
        List<String> productIds = new ArrayList<>();
        try (Connection connection = SqlConnection.getDestinationConnection()) {
            String query = "SELECT id FROM deactivate";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    productIds.add(id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Рассмотрите возможность использования логгера вместо printStackTrace
        }
        return productIds;
    }

    private void excelEx(String id) {
        try (Connection connection = SqlConnection.getSourceConnection()) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Results");

            // Create header row for Products information
            Row productHeaderRow = sheet.createRow(0);
            productHeaderRow.createCell(0).setCellValue("Product Id");
            productHeaderRow.createCell(1).setCellValue("Product Name");
            productHeaderRow.createCell(2).setCellValue("OrganizationTin");

            int rowNum = 1; // Start with the second row for FacilityProduct and Facilities information

            // Continue with the query for FacilityProduct and Facilities
            String facilityQuery = "SELECT \"Facilities\".\"Name\", \"Facilities\".\"Address\", \"Facilities\".\"SubjectTin\", \"FacilityProduct\".\"Amount\" FROM \"FacilityProduct\" INNER JOIN \"Facilities\" ON \"Facilities\".\"Id\" = \"FacilityProduct\".\"FacilityId\" WHERE \"FacilityProduct\".\"ProductId\" = ? ORDER BY \"FacilityProduct\".\"Amount\" DESC;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(facilityQuery)) {
                preparedStatement.setObject(1, UUID.fromString(id));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        // No data found for FacilityProduct and Facilities, no need to proceed
                        System.out.println("No data found for FacilityProduct and Facilities. Excel file not created.");
                        return;
                    }

                    do {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(resultSet.getString("Name"));
                        row.createCell(1).setCellValue(resultSet.getString("Address"));
                        row.createCell(2).setCellValue(resultSet.getString("SubjectTin"));
                        row.createCell(3).setCellValue(resultSet.getDouble("Amount"));
                    } while (resultSet.next());

                    // Execute the additional SELECT statement for Products only if FacilityProduct and Facilities data is present
                    String productQuery = "SELECT \"Id\", \"Name\", \"OrganizationTin\" FROM \"Products\" WHERE \"Id\" = ?";
                    try (PreparedStatement productStatement = connection.prepareStatement(productQuery)) {
                        productStatement.setObject(1, UUID.fromString(id));

                        try (ResultSet productResultSet = productStatement.executeQuery()) {
                            if (productResultSet.next()) {
                                // Populate the first row with Products information
                                Row productRow = sheet.createRow(0);
                                productRow.createCell(0).setCellValue(productResultSet.getString("Id"));
                                productRow.createCell(1).setCellValue(productResultSet.getString("Name"));
                                productRow.createCell(2).setCellValue(productResultSet.getString("OrganizationTin"));
                            }
                        }
                    }

                    // Используем переменную iteration в имени файла
                    String filePath = Paths.get(System.getProperty("user.home"), "Documents", "Product" + iteration + ".xlsx").toString();
                    try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                        workbook.write(fileOut);
                    }

                    System.out.println("Excel file created successfully at: " + filePath);

                    // Увеличиваем значение iteration для следующего вызова
                    iteration++;
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }



}
