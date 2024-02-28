package org.example.api.helper;



import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.api.Entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Helper {



    public static boolean checkExcelFormat(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
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

    public static List<Product> convertExcelToListOfProduct(InputStream is, List<String> sheetNames) {
        List<Product> list = new ArrayList<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(is);

            // Iterate through each provided sheet name
            for (String sheetName : sheetNames) {
                // Trim the sheet name to remove leading/trailing whitespace
                sheetName = sheetName.trim();

                XSSFSheet sheet = workbook.getSheet(sheetName);

                if (sheet != null) {
                    Iterator<Row> iterator = sheet.iterator();
                    int rowNumber = 0;

                    while (iterator.hasNext()) {
                        Row row = iterator.next();

                        // Skip header row
                        if (rowNumber == 0) {
                            rowNumber++;
                            continue;
                        }

                        Iterator<Cell> cells = row.iterator();
                        int cid = 0;
                        Product p = new Product();

                        while (cells.hasNext()) {
                            Cell cell = cells.next();

                            switch (cid) {
                                case 0:
                                    p.setId((int) cell.getNumericCellValue());
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
                                    p.setProductPrice(cell.getNumericCellValue());
                                    break;
                                default:
                                    break;
                            }
                            cid++;
                        }

                        list.add(p);
                        rowNumber++;
                    }
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
    }


