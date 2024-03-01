package org.example.api.repo;

import org.example.api.Entity.Pdf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Pdfrepo extends JpaRepository<Pdf,Integer> {

    Pdf findAllByFilename(String filename);


}
