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
import java.util.NoSuchElementException;

public class TempConsider {
    private WebDriver webDriver = null;

    public void Start () {
        webDriver = new ChromeDriver();
        loginSet("https://insp.salyk.kg/ettn/productrequest", "admin", "Qwerty_123!@#");
        Map<String, String> map = fetchTempProductName();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            processEntry(entry.getKey(), entry.getValue());
        }
        webDriver.close();
    }
    private void processEntry(String key, String value) {

        try {
            webDriver.get("https://insp.salyk.kg/ettn/productrequest/action/" + key);
            Thread.sleep(250);
            if (value != null) {
                webDriver.findElement(By.xpath("//*[@id=\"with-input\"]")).click();
                Thread.sleep(100);
                webDriver.findElement(By.xpath("/html/body/div[4]/div/textarea")).sendKeys(value);
                Thread.sleep(200);
                webDriver.findElement(By.xpath("/html/body/div[4]/div/button[1]")).click();
            }else{
                String currentUrl = webDriver.getCurrentUrl();
                webDriver.findElement(By.xpath("/html/body/div[1]/div[3]/div/div[3]/div[1]/div/div/div[7]/div[2]/form/button[1]")).click();
                if (currentUrl == webDriver.getCurrentUrl()){
                    String errorText = webDriver.findElement(By.xpath("/html/body/div[2]/div/div[1]/div[2]")).getText();
                    webDriver.findElement(By.xpath("//*[@id=\"with-input\"]")).click();
                    Thread.sleep(100);
                    webDriver.findElement(By.xpath("/html/body/div[4]/div/textarea")).sendKeys(errorText);
                    Thread.sleep(200);
                    webDriver.findElement(By.xpath("/html/body/div[4]/div/button[1]")).click();
                }
            }

        } catch (NoSuchElementException ignored) {
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            webDriver.navigate().refresh();
        }
    }
    private Map<String, String> fetchTempProductName() {
        Map<String, String> pairMap = new HashMap<>();
        try (Connection connection = SqlConnection.getDestinationConnection();) {
            String query = "SELECT \"id\", comment FROM \"tempproduct\" ORDER BY barcode";
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
}
