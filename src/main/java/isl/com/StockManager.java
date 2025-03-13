package isl.com;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StockManager {
    private static final String vendor = "stock.xlsx";
    private static Map<String, StockItem> stock = new HashMap<>();

    static {
        loadStockFromExcel();
    }

    public static void addItem(String item, int quantity, double price) {
        stock.put(item, new StockItem(item, quantity, price));
        saveStockToExcel();
    }

    public static void removeItem(String item) {
        stock.remove(item);
        saveStockToExcel();
    }

    public static void updateItem(String item, int quantity, double price) {
        if (stock.containsKey(item)) {
            stock.put(item, new StockItem(item, quantity, price));
            saveStockToExcel();
        }
    }

    public static Map<String, StockItem> getStock() {
        return stock;
    }

    private static void saveStockToExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Stock");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Item");
            headerRow.createCell(1).setCellValue("Quantity");
            headerRow.createCell(2).setCellValue("Price");

            int rowNum = 1;
            for (StockItem item : stock.values()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getName());
                row.createCell(1).setCellValue(item.getQuantity());
                row.createCell(2).setCellValue(item.getPrice());
            }

            try (FileOutputStream fileOut = new FileOutputStream(vendor)) {
                workbook.write(fileOut);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadStockFromExcel() {
        File file = new File(vendor);
        if (!file.exists()) return;

        try (FileInputStream fileIn = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fileIn)) {

            Sheet sheet = workbook.getSheetAt(0);
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
}
