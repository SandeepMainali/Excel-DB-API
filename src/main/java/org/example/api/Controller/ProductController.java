package org.example.api.Controller;

import org.example.api.Entity.Pdf;
import org.example.api.Entity.Product;
import org.example.api.helper.Helper;
import org.example.api.repo.Pdfrepo;
import org.example.api.service.PdfService;
import org.example.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;


@Controller

public class ProductController {

    //  @Autowired
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
     PdfService pdfService;


    @PostMapping("/upload")
    public ResponseEntity<?> handleUpload(@RequestParam("file") MultipartFile file, @RequestParam(value = "sheetNames", required = false) List<String> sheetNames) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        try {
            if (Helper.checkExcelFormat(file)) {
                if (sheetNames != null && !sheetNames.isEmpty()) {
                    productService.save(file, sheetNames);
                } else {
                    productService.save(file, Helper.getAllSheetNames(file));
                }
                return ResponseEntity.ok(Map.of("message", "File is uploaded and data is saved to db"));
            } else {
                pdfService.unzipAndSaveToDatabase(file.getBytes());
                return ResponseEntity.ok("File has been successfully unzipped and saved to the database.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process the file: " + e.getMessage());
        }
    }


    @GetMapping("/all")
    public ResponseEntity<?> getProducts(@RequestParam(required = false) String sheetName) {
        if (sheetName != null) {
            // Retrieve all products based on the provided sheet name
            List<Product> products = productService.getAllProductsBySheetName(sheetName);

            // Check if products are found
            if (!products.isEmpty()) {
                return ResponseEntity.ok(products);
            } else {
                return ResponseEntity.notFound().build(); // No products found for the provided sheet name
            }
        } else {
            return ResponseEntity.badRequest().body("Sheet name parameter is required."); // Sheet name parameter is required
        }
    }

    @GetMapping("/product")
    public String getAllProduct(Model model) {
        List<Product> products = productService.getAllProducts();
        List<Pdf> pdfs = pdfService.getAllPdf();

        // Combine products and pdfs into a single list
        List<Object> combinedList = new ArrayList<>();
        combinedList.addAll(products);
        combinedList.addAll(pdfs);

        model.addAttribute("combinedList", combinedList);
        return "index"; // This should return the name of your HTML template
    }


    @GetMapping("/view-pdf/{filename}")
    public ResponseEntity<ByteArrayResource> viewPdf(@PathVariable String filename) {
        byte[] pdfContent = pdfService.getPdfContent(filename);

        if (pdfContent != null) {
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename + ".pdf"); // Set filename with .pdf extension

            // Return PDF content as a byte array resource
            ByteArrayResource resource = new ByteArrayResource(pdfContent);
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }





}


