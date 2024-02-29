package org.example.api.Controller;

import org.example.api.Entity.Product;
import org.example.api.helper.Helper;
import org.example.api.service.ProductService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
                productService.unzipAndSaveToDatabase(file.getBytes());
                return ResponseEntity.ok("File has been successfully unzipped and saved to the database.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process the file: " + e.getMessage());
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String sheetName) {
        List<Product> products;
        if (sheetName != null) {
            products = productService.getAllProductsBySheetName(sheetName);
        } else {
            products = productService.getAllProducts();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/product")
    public String getAllProduct(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "index"; // This should return the name of your HTML template
    }

    @GetMapping("/view-pdf/{filename}")
    public ResponseEntity<byte[]> viewPdf(@PathVariable String filename) {
        byte[] pdfContent = productService.getPdfContent(filename);
        if (pdfContent != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(filename, filename);
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }





}


