package org.example.api.repo;

import org.example.api.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product,Integer> {
    Product findAllByFilename(String filename);
    List<Product> findAllBySheetName(String sheetName);



}
