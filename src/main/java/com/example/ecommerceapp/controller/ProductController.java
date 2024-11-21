package com.example.ecommerceapp.controller;

import com.example.ecommerceapp.model.dto.ProductRequestDTO;
import com.example.ecommerceapp.model.entities.Product;
import com.example.ecommerceapp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping( "/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Tüm Ürünleri Listeleme Endpointi
     *
     * @return Tüm ürünlerin listesi
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Belirli Bir Ürünü Getirme Endpointi
     *
     * @param id Ürün ID'si
     * @return Ürün bilgisi veya bulunamadı mesajı
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Product id not found"));
        }
    }

    /**
     * Yeni Ürün Oluşturma Endpointi
     *
     * @param productRequest Eklenecek ürün bilgisi
     * @return Oluşturulan ürün
     */
    @PostMapping("/createProduct")
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequestDTO productRequest) {
        Product newProduct = new Product();
        newProduct.setName(productRequest.name());
        newProduct.setPrice(productRequest.price());
        newProduct.setDescription(productRequest.description());
        Product created = productService.createProduct(newProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Ürün Güncelleme Endpointi
     *
     * @param id             Ürün ID'si
     * @param productRequest Güncellenmiş ürün detayları
     * @return Güncellenmiş ürün veya bulunamadı mesajı
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO productRequest) {
        try {
            Product updated = productService.updateProduct(id,
                    new Product(productRequest.name(), productRequest.description(), productRequest.price())
            );
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Product not found"));
        }
    }

    /**
     * Ürün Silme Endpointi
     *
     * @param id Silinecek ürün ID'si
     * @return Başarılı veya bulunamadı mesajı
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Product not found"));
        }
    }
}
