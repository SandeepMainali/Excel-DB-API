package org.example.api.helper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.api.Entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Helper {

    public static boolean checkExcelFormat(MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        } else {
            return false;
        }

    }

    public static List<Product> convertExcelToListOfProduct(InputStream is) {
        List<Product> list = new ArrayList<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheet("sheet1");

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
                                p.setProductId((int) cell.getNumericCellValue());
                                break;
                            case 1:
                                p.setProductName(cell.getStringCellValue());
                                break;
                            case 2:
                                p.setProductDesc(cell.getStringCellValue());
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
                System.out.println("Sheet 'data' not found in Excel file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}
