package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class esf {


    public static void main(String[] args) throws InterruptedException {
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("https://testesf.salyk.kg/esf/view/document/realization_form.xhtml?cid=4");
        webDriver.findElement(By.xpath("//*[contains(text(),'Вход через логин и пароль')]")).click();
        webDriver.findElement(By.id("username")).sendKeys("shama1");
        webDriver.findElement(By.id("password")).sendKeys("QWEasdzxc123!");
        webDriver.findElement(By.cssSelector("input[value='Войти']")).click();
        webDriver.get("https://testesf.salyk.kg/esf/view/document/realization_list.xhtml");
        webDriver.findElement(By.xpath("//*[contains(text(),'Добавить')]")).click();

        webDriver.findElement(By.id("receiptType_input")).clear();
        webDriver.findElement(By.id("receiptType_input")).sendKeys("Акт об оказании услуг");

        webDriver.findElement(By.id("deliveryDate_input")).clear();
        webDriver.findElement(By.id("deliveryDate_input")).sendKeys("24-11-2023");

        webDriver.findElement(By.id("VATDeliveryType")).findElement(By.cssSelector("button.ui-button")).click();
        webDriver.findElement(By.id("VATDeliveryType_panel")).findElement(By.xpath("//td[text()='освобожденная \"0\"']")).click();

        webDriver.findElement(By.id("contractor_input")).sendKeys("99999999999999 - ККМ");
//        Thread.sleep(10000);
//        webDriver.findElement(By.xpath("//*[contains(text(),'02209201610022')]")).click();


        webDriver.findElement(By.id("bankAccountSeller_input")).sendKeys("4404011101003303 - МП  Ак-Суйское КХ");
//        webDriver.findElement(By.xpath("//*[contains(text(),'1130090000001952')]")).click();

        webDriver.findElement(By.id("bankAccountBuyer_input")).sendKeys("987654321 - РСК банк");

//        webDriver.findElement(By.id("deliveryContractNumber")).sendKeys("№185-09/01/2023");
        webDriver.findElement(By.id("deliveryContractDate_input")).sendKeys("09-01-2023");
//        webDriver.findElement(By.id("note")).sendKeys("Your note here");

        webDriver.findElement(By.id("invoiceDeliveryType_input")).sendKeys("223 - (Утратила силу в соответствии с постановлением Правительства КР от 4 июня 2018 года № 268)");
        Thread.sleep(1000);
        webDriver.findElement(By.id("paymentType_input")).sendKeys("Безналичная");
        Thread.sleep(1000);
        webDriver.findElement(By.id("VAT_input")).sendKeys("Освобожденная");

        webDriver.findElement(By.name("j_idt197")).click();
        Thread.sleep(10000);

    }
}
