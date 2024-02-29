package org.example.api.service;
import jakarta.transaction.Transactional;
import org.example.api.Entity.Product;
import org.example.api.helper.Helper;
import org.example.api.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
@Service
public class ProductService {

    private final ProductRepo productRepo;

    @Autowired
    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }


    public List<Product> getAllProductsBySheetName(String sheetName) {
        return productRepo.findAllBySheetName(sheetName);
    }

    public void save(MultipartFile file, List<String> sheetNames) {
        try {
            Helper helper = new Helper(productRepo); // Instantiate the Helper class
            for (String sheetName : sheetNames) {
                // Check if products from this sheet already exist in the database
                List<Product> existingProducts = productRepo.findAllBySheetName(sheetName);

                if (!existingProducts.isEmpty()) {
                    System.out.println("Products from sheet '" + sheetName + "' already exist in the database. Skipping.");
                    continue; // Skip processing this sheet
                }

                // Convert Excel data to Product entities and save to the database
                List<Product> products = helper.convertExcelToListOfProduct(file.getInputStream(), Collections.singletonList(sheetName));
                productRepo.saveAll(products);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<Product> getAllProducts() {

        return this.productRepo.findAll();

    }

    @Transactional
    public void unzipAndSaveToDatabase(byte[] zipBytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(zipBytes);
             ZipInputStream zis = new ZipInputStream(bis)) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory() && isPdfFile(zipEntry.getName())) {
                    byte[] pdfContent = readEntryContent(zis);
                    String filename = getFilename(zipEntry.getName());
                    savePdfAsProduct(filename, pdfContent);
                }
                zipEntry = zis.getNextEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while unzipping and saving to database: " + e.getMessage(), e);
        }
    }

    private boolean isPdfFile(String fileName) {
        return fileName.toLowerCase().endsWith(".pdf");
    }

    private byte[] readEntryContent(ZipInputStream zis) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = zis.read(buffer)) > 0) {
            bos.write(buffer, 0, len);
        }
        return bos.toByteArray();
    }

    private String getFilename(String filePath) {
        int lastSlashIndex = filePath.lastIndexOf("/");
        if (lastSlashIndex != -1) {
            return filePath.substring(lastSlashIndex + 1);
        } else {
            return filePath;
        }
    }

    @Transactional
    protected void savePdfAsProduct(String filename, byte[] fileContent) {
        Product product = new Product();
        product.setFilename(filename);
        product.setCreateAt(LocalDateTime.now());
        product.setContent(fileContent);
        productRepo.save(product);

    }

    @Transactional
    public byte[] getPdfContent(String filename) {
        Product product = productRepo.findAllByFilename(filename);
        if (product != null) {
            return product.getContent();
        } else {
            return null;
        }
    }







}


