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
    }
    public void Start (Map<String, String> mapp) {
        webDriver = new ChromeDriver();
        loginSet("https://gepir.gs1.org/index.php/search-by-gtin", "a.dolubaev@it.salyk.kg", "69vM?7]8");
        Map<String, String> map;
        map = mapp;

        for (Map.Entry<String, String> entry : map.entrySet()) {
            processEntry(entry.getKey(), entry.getValue());
        }

        webDriver.quit();
    }

    private void loginSet(String url,String login, String password) {
        try {
            webDriver.get(url);
            webDriver.findElement(By.id("onetrust-accept-btn-handler")).click();
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
            } else if (webDriver.getPageSource().contains("internal use only"))  {
                throw new InvalidElementStateException();
            }

            keyValueElement.clear();
        } catch (NoSuchElementException | InterruptedException e) {
            updateComment(key, "Незарегистрированный штрих код");
        } catch (InvalidElementStateException e) {
                updateComment(key, "Внутренний штрих код");
        } finally {
            webDriver.navigate().refresh();
        }
    }
    private void updateComment(String key, String comm) {
        String updateQuery = "UPDATE tempproduct SET comment = ? WHERE id = ?";

        try {
            synchronized (this) {
                try (Connection connection = SqlConnection.getDestinationConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

                    preparedStatement.setString(1, comm);
                    preparedStatement.setString(2, key);
                    int rowsAffected = preparedStatement.executeUpdate();

//                    if (rowsAffected > 0) {
//                        System.out.println("Comment updated successfully for key: " + key);
//                    } else {
//                        System.out.println("No rows were updated for key: " + key);
//                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL exception occurred: " + e.getMessage());
        }
    }
}