package org.example.api.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.sql.Blob;
import java.time.LocalDateTime;


@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_desc")
    private String productDesc;

    @Column(name = "product_price")
    private double productPrice;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
//    private Double productPrice;

    @Column(name = "filename")
    private String filename;

    @NotNull
    @JsonIgnore
    @Column(updatable = false)
    private LocalDateTime createAt;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


    @Lob
    @Column(name = "content", columnDefinition = "LONGBLOB")
    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }


    public Product() {
    }



    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }
}
