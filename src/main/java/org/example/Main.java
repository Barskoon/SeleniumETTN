package org.example;

public class Main {
    public static void main(String[] args) {
//        DataTransfer dt = new DataTransfer();
//        dt.Start();
//        MapSplitter mp = new MapSplitter();
//        mp.gsThread(dt.fetchIdBarcode(), 2);
//        InspectName i = new InspectName();i.Inspect();


        DataTransfer dt = new DataTransfer();
        MapSplitter mp = new MapSplitter();
//        InspectName i = new InspectName();i.Inspect();
        mp.tempConsiderThread(dt.fetchIdComm(), 4);

    }
}