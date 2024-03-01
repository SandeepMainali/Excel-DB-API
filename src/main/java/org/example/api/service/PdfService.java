package org.example.api.service;

import jakarta.transaction.Transactional;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.example.api.Entity.Pdf;
import org.example.api.Entity.Product;
import org.example.api.repo.Pdfrepo;
import org.example.api.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class PdfService {

@Autowired
     Pdfrepo pdfrepo;


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
        Pdf pdf = new Pdf();
        String truncatedFilename = truncateFilename(filename);
        pdf.setFilename(truncatedFilename);
        pdf.setExtension(extractExtension(truncatedFilename));
        pdf.setCreateAt(LocalDateTime.now());
        pdf.setContent(fileContent);
        pdfrepo.save(pdf);
    }

private String truncateFilename(String filename) {
    if (filename.length() > 15) {
        return filename.substring(0, 15);
    }
    return filename;
}

    private String extractExtension(String filename) {
        return ".pdf";
    }

    @Transactional
    public byte[] getPdfContent(String filename) {
        Pdf p = pdfrepo.findAllByFilename(filename);
        if (p != null) {
            return p.getContent();
        } else {
            return null;
        }
    }

    public List<Pdf> getAllPdf() {
        return this.pdfrepo.findAll();
    }


}


