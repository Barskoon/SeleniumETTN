package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Deactivate {
    private WebDriver webDriver = null;
    public static void main(String[] args) {
        Deactivate d = new Deactivate();
        d.Start();
        //d.clearDB();


    }
    public void Start () {
        webDriver = new ChromeDriver();
        loginSet("https://insp.salyk.kg/ettn/product", "admin", "Qwerty_123!@#");
        Map<String, String> map = fetchTempProductName();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            processEntry(entry.getKey(), entry.getValue());
        }

        webDriver.quit();
    }
    private void processEntry(String key, String value) {
        WebElement id;
        WebElement selectInformation;
        WebElement selectButton;
        WebElement searchButton;

        try {
            webDriver.get("http://insp.salyk.kg/ettn/product/deactivate/" + key);
            webDriver.findElement(By.id("Reason")).sendKeys(value);
            webDriver.findElement(By.xpath("//*[@type='submit']")).click();
            System.out.println(key);

        } catch (org.openqa.selenium.NoSuchElementException e) {
            System.out.println(e);
        } finally {
            webDriver.navigate().refresh();
        }
    }
    private Map<String, String> fetchTempProductName() {
        Map<String, String> pairMap = new HashMap<>();
        try (Connection connection = SqlConnection.getDestinationConnection();) {
            String query = "SELECT id, comment FROM deactivate";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String id = resultSet.getString("id");
                        String comment = resultSet.getString("comment");
                        pairMap.put(id, comment);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pairMap;
    }
    private void loginSet(String url,String login, String password) {
        try {
            webDriver.get(url);
            webDriver.findElement(By.id("Username")).sendKeys(login);
            webDriver.findElement(By.id("Password")).sendKeys(password);
            webDriver.findElement(By.xpath("//*[@type='submit']")).click();
            webDriver.manage().window().maximize();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearDB() {
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
}
