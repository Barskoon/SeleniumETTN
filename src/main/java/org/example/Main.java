package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {
    public static void main(String[] args) {
        DataTransfer dt = new DataTransfer();
        dt.Start();
        MapSplitter mp = new MapSplitter();
        InspectName i = new InspectName();i.Inspect();

//        mp.gsThread(dt.fetchIdBarcode(), 2);
//        DataTransfer dt = new DataTransfer();
//        MapSplitter mp = new MapSplitter();
//        mp.tempConsiderThread(dt.fetchIdComm(), 4);

    }
}