package org.example.api.repo;

import org.example.api.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product,Integer> {
    Product findByFilename(String filename);

}
