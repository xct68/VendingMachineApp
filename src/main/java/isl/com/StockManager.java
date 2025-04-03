package isl.com;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StockManager {
    private static final String VENDOR_FILE = "stock.xlsx";
    private static Map<String, StockItem> stock = new HashMap<>();
    private static String activeClient = "Client 1";

    static {
        loadStockFromExcel(VENDOR_FILE);
    }

    public static void setActiveClient(String clientName) {
        activeClient = clientName;
        loadStockFromExcel(VENDOR_FILE);
    }

    public static Map<String, StockItem> getStock() {
        return stock;
    }

    public static void addItem(String itemName, int quantity, double price) {
        StockItem newItem = new StockItem(itemName, quantity, price);
        stock.put(itemName, newItem);
        saveStockToExcel(VENDOR_FILE);
    }

    public static void addClientSheet(String clientName) {
        try (FileInputStream fileIn = new FileInputStream(VENDOR_FILE);
             Workbook workbook = new XSSFWorkbook(fileIn)) {

            if (workbook.getSheet(clientName) == null) {
                Sheet newSheet = workbook.createSheet(clientName);
                Row header = newSheet.createRow(0);
                header.createCell(0).setCellValue("Item");
                header.createCell(1).setCellValue("Quantity");
                header.createCell(2).setCellValue("Price");

                try (FileOutputStream fileOut = new FileOutputStream(VENDOR_FILE)) {
                    workbook.write(fileOut);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadStockFromExcel(String filePath) {
        try (FileInputStream fileIn = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileIn)) {

            // Ensure client sheet exists, or create it
            Sheet sheet = workbook.getSheet(activeClient);
            if (sheet == null) {
                System.out.println("Sheet for " + activeClient + " not found. Creating a new one.");
                addClientSheet(activeClient);
                // Reopen the file to get the updated workbook with the new sheet
                try (FileInputStream fileIn2 = new FileInputStream(filePath);
                     Workbook workbook2 = new XSSFWorkbook(fileIn2)) {
                    sheet = workbook2.getSheet(activeClient);
                }
            }

            if (sheet == null) {
                throw new IOException("Failed to create or retrieve the sheet: " + activeClient);
            }

            stock.clear();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String item = row.getCell(0).getStringCellValue();
                int quantity = (int) row.getCell(1).getNumericCellValue();
                double price = row.getCell(2).getNumericCellValue();
                stock.put(item, new StockItem(item, quantity, price));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveStockToExcel(String filePath) {
        try (FileInputStream fileIn = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileIn)) {

            // Get or create the active client's sheet
            Sheet sheet = workbook.getSheet(activeClient);
            if (sheet == null) {
                sheet = workbook.createSheet(activeClient);
            }

            // Clear existing rows
            int lastRow = sheet.getLastRowNum();
            for (int i = lastRow; i >= 0; i--) {
                sheet.removeRow(sheet.getRow(i));
            }

            // Create header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Item");
            header.createCell(1).setCellValue("Quantity");
            header.createCell(2).setCellValue("Price");

            int rowNum = 1;
            for (StockItem item : stock.values()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getName());
                row.createCell(1).setCellValue(item.getQuantity());
                row.createCell(2).setCellValue(item.getPrice());
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
