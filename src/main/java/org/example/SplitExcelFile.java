package org.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SplitExcelFile {

    private static final int MAX_RECORDS_PER_FILE = 5000;

    public static void main(String[] args) {
        String inputFile = "C:/Users/Imambek/Documents/Untitled.xlsx";
        String outputFolder = "C:/Users/Imambek/Documents/";

        try {
            splitExcelFile(inputFile, outputFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void splitExcelFile(String inputFile, String outputFolder) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            int sheetCount = workbook.getNumberOfSheets();

            for (int i = 0; i < sheetCount; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                int rowCount = sheet.getPhysicalNumberOfRows();

                Workbook newWorkbook = new XSSFWorkbook();
                Sheet newSheet = newWorkbook.createSheet(sheetName);

                int recordsInCurrentFile = 0;
                int fileNumber = 1;

                // Copy the first row to each file
                Row firstRow = sheet.getRow(0);
                Row newFirstRow = newSheet.createRow(0);
                int cellCount = firstRow.getPhysicalNumberOfCells();

                for (int k = 0; k < cellCount; k++) {
                    Cell sourceCell = firstRow.getCell(k);
                    Cell newCell = newFirstRow.createCell(k);

                    if (sourceCell != null) {
                        CellType cellType = sourceCell.getCellType();
                        newCell.setCellType(cellType);

                        switch (cellType) {
                            case STRING:
                                newCell.setCellValue(sourceCell.getStringCellValue());
                                break;
                            case NUMERIC:
                                newCell.setCellValue(sourceCell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                newCell.setCellValue(sourceCell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                newCell.setCellFormula(sourceCell.getCellFormula());
                                break;
                            case BLANK:
                                // Do nothing for blank cells
                                break;
                            default:
                                // Do nothing for other cell types
                        }
                    }
                }

                recordsInCurrentFile++;

                for (int j = 1; j < rowCount; j++) {
                    Row sourceRow = sheet.getRow(j);
                    Row newRow = newSheet.createRow(recordsInCurrentFile);

                    for (int k = 0; k < cellCount; k++) {
                        Cell sourceCell = sourceRow.getCell(k);
                        Cell newCell = newRow.createCell(k);

                        if (sourceCell != null) {
                            CellType cellType = sourceCell.getCellType();
                            newCell.setCellType(cellType);

                            switch (cellType) {
                                case STRING:
                                    newCell.setCellValue(sourceCell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    newCell.setCellValue(sourceCell.getNumericCellValue());
                                    break;
                                case BOOLEAN:
                                    newCell.setCellValue(sourceCell.getBooleanCellValue());
                                    break;
                                case FORMULA:
                                    newCell.setCellFormula(sourceCell.getCellFormula());
                                    break;
                                case BLANK:
                                    // Do nothing for blank cells
                                    break;
                                default:
                                    // Do nothing for other cell types
                            }
                        }
                    }

                    recordsInCurrentFile++;

                    if (recordsInCurrentFile == MAX_RECORDS_PER_FILE) {
                        String outputFilePath = outputFolder + fileNumber + ".xlsx";
                        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                            newWorkbook.write(fos);
                        }

                        // Reset counters for the next file
                        newWorkbook = new XSSFWorkbook();
                        newSheet = newWorkbook.createSheet(sheetName);

                        // Copy the first row to each file
                        newFirstRow = newSheet.createRow(0);
                        for (int k = 0; k < cellCount; k++) {
                            Cell sourceCell = firstRow.getCell(k);
                            Cell newCell = newFirstRow.createCell(k);

                            if (sourceCell != null) {
                                CellType cellType = sourceCell.getCellType();
                                newCell.setCellType(cellType);

                                switch (cellType) {
                                    case STRING:
                                        newCell.setCellValue(sourceCell.getStringCellValue());
                                        break;
                                    case NUMERIC:
                                        newCell.setCellValue(sourceCell.getNumericCellValue());
                                        break;
                                    case BOOLEAN:
                                        newCell.setCellValue(sourceCell.getBooleanCellValue());
                                        break;
                                    case FORMULA:
                                        newCell.setCellFormula(sourceCell.getCellFormula());
                                        break;
                                    case BLANK:
                                        // Do nothing for blank cells
                                        break;
                                    default:
                                        // Do nothing for other cell types
                                }
                            }
                        }

                        recordsInCurrentFile = 1; // Reset to 1 as the first row is already copied
                        fileNumber++;
                    }
                }

                // Write the remaining records to the last file
                if (recordsInCurrentFile > 0) {
                    String outputFilePath = outputFolder + fileNumber + ".xlsx";
                    try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                        newWorkbook.write(fos);
                    }
                }
            }

            System.out.println("Splitting Excel file completed successfully.");
        }
    }
}
