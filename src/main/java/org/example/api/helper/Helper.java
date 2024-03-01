package org.example.api.helper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.api.Entity.Product;
import org.example.api.repo.ProductRepo;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Helper {

    private final ProductRepo productRepo;


    public Helper(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public static boolean checkExcelFormat(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }
    public List<Product> convertExcelToListOfProduct(InputStream is, List<String> sheetNames) {
        List<Product> list = new ArrayList<>();
        Set<String> processedSheetNames = new HashSet<>(); // Store processed sheet names

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(is);

            for (String sheetName : sheetNames) {
                sheetName = sheetName.trim();

                // Check if the sheet name has already been processed
                if (processedSheetNames.contains(sheetName)) {
                    System.out.println("Sheet '" + sheetName + "' has already been processed. Skipping.");
                    continue;
                }

                XSSFSheet sheet = workbook.getSheet(sheetName);

                if (sheet != null) {
                    Iterator<Row> iterator = sheet.iterator();
                    int rowNumber = 0;

                    while (iterator.hasNext()) {
                        Row row = iterator.next();

                        if (rowNumber == 0) {
                            rowNumber++;
                            continue;
                        }

                        Iterator<Cell> cells = row.iterator();
                        int cid = 0;
                        Product p = new Product();

                        // Set sheetName for each Product
                        p.setSheetName(sheetName);

                        while (cells.hasNext()) {
                            Cell cell = cells.next();

                            switch (cid) {
                                case 0: // Assuming productId is in the first column
                                    int productId = (int) cell.getNumericCellValue();
                                    p.setProductId(productId);
                                    break;
                                case 1:
                                    String productName = cell.getStringCellValue();
                                    if (productName != null) {
                                        p.setProductName(productName);
                                    }
                                    break;
                                case 2:
                                    String productDesc = cell.getStringCellValue();
                                    if (productDesc != null) {
                                        p.setProductDesc(productDesc);
                                    }
                                    break;
                                case 3:
                                    Double productPrice = cell.getNumericCellValue();
                                    p.setProductPrice(productPrice);
                                    break;
                                default:
                                    break;
                            }
                            cid++;
                        }

                        list.add(p);
                        rowNumber++;
                    }

                    // Mark the sheet name as processed
                    processedSheetNames.add(sheetName);
                } else {
                    System.out.println("Sheet '" + sheetName + "' not found in Excel file.");
                }
            }
        } catch (IOException e) {
            // Handle IOException appropriately
            e.printStackTrace();
        } catch (Exception e) {
            // Handle other exceptions gracefully
            e.printStackTrace();
        }

        return list;
    }

    public static List<String> getAllSheetNames(MultipartFile file) throws IOException {
        List<String> sheetNames = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetName(i));
            }
        }
        return sheetNames;
    }
}