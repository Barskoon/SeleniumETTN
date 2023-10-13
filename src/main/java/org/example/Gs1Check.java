package org.example;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Gs1Check {

    private WebDriver webDriver = null;
    public Gs1Check() {
        webDriver = new ChromeDriver();
        loginSet("https://gepir.gs1.org/index.php/search-by-gtin", "a.dolubaev@it.salyk.kg", "69vM?7]8");
        Map<String, String> map = fetchTempProduct();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            processEntry(entry.getKey(), entry.getValue());
        }

        webDriver.quit();
    }
    private void loginSet(String url,String login, String password) {
        try {
            webDriver.get(url);
            webDriver.findElement(By.id("uname")).sendKeys(login);
            webDriver.findElement(By.id("pword")).sendKeys(password);
            webDriver.findElement(By.id("login-button")).click();
            webDriver.manage().window().maximize();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void processEntry(String key, String value) {
        try {
            Thread.sleep(250);
            WebElement keyValueElement = webDriver.findElement(By.id("keyValue"));
            keyValueElement.sendKeys(value);
            Thread.sleep(250);
            webDriver.findElement(By.id("submit-button")).click();
            Thread.sleep(2500);

            if (webDriver.getPageSource().contains("alert-dismissible")) {
                throw new NoSuchElementException();
            }

            keyValueElement.clear();
        } catch (NoSuchElementException | InterruptedException e) {
            updateComment(key, "Незарегистрированный штрих код");
        } catch (InvalidElementStateException e) {
            if (webDriver.getPageSource().contains("internal use only"))
                updateComment(key, "Внутренний штрих код");
        } finally {
            webDriver.navigate().refresh();
        }
    }
    private void updateComment(String key, String comm) {
        try (Connection connection = SqlConnection.getDestinationConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE tempproduct SET comment = ? WHERE id = ?")) {

            preparedStatement.setString(1, comm);
            preparedStatement.setString(2, key);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private Map<String, String> fetchTempProduct() {
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