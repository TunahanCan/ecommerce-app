package com.example.ecommerceapp.controller;

import com.example.ecommerceapp.model.dto.ApiResponseDTO;
import com.example.ecommerceapp.model.dto.ProductRequestDTO;
import com.example.ecommerceapp.model.entities.Product;
import com.example.ecommerceapp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/product")
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
    @GetMapping("/getAll")
    public ResponseEntity<ApiResponseDTO<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(
                ApiResponseDTO.<List<Product>>builder()
                        .success(true)
                        .message("Product list retrieved.")
                        .data(products)
                        .build()
        );
    }

    /**
     * Belirli Bir Ürünü Getirme Endpointi
     *
     * @param id Ürün ID'si
     * @return Ürün bilgisi veya bulunamadı mesajı
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponseDTO<Product>> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(value -> ResponseEntity.ok(
                ApiResponseDTO.<Product>builder()
                        .success(true)
                        .message("Product  retrieved.")
                        .data(value)
                        .build()
        )).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDTO<>(false, "Product not found.", null)));
    }

    /**
     * Ürün olusturma endpointi
     *
     * @param productRequest
     * @return
     */
    @PostMapping("/createProduct")
    public ResponseEntity<ApiResponseDTO<Product>> createProduct(@RequestBody ProductRequestDTO productRequest) {
        Product newProduct = new Product();
        newProduct.setName(productRequest.name());
        newProduct.setPrice(productRequest.price());
        newProduct.setDescription(productRequest.description());
        try {
            Product createdProduct = productService.createProduct(newProduct);
            ApiResponseDTO<Product> response = new ApiResponseDTO<>(
                    true,
                    "Product created successfully.",
                    createdProduct
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ApiResponseDTO<Product> response = new ApiResponseDTO<>(
                    false,
                    "Failed to create product: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Ürün Güncelleme Endpoint'i
     *
     * @param id             Ürün ID'si
     * @param productRequest Güncellenmiş ürün detayları
     * @return Güncellenmiş ürün veya bulunamadı mesajı
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponseDTO<Product>> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO productRequest) {
        try {
            Product updatedProduct = productService.updateProduct(id,
                    new Product(productRequest.name(), productRequest.description(), productRequest.price())
            );
            ApiResponseDTO<Product> response = new ApiResponseDTO<>(
                    true,
                    "Product updated successfully.",
                    updatedProduct
            );
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            ApiResponseDTO<Product> response = new ApiResponseDTO<>(
                    false,
                    "Product not found.",
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Ürün Silme Endpointi
     *
     * @param id Silinecek ürün ID'si
     * @return Başarılı veya bulunamadı mesajı
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(
                    ApiResponseDTO.<Void>builder()
                            .success(true)
                            .message("deleted product :" + id)
                            .data(null)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponseDTO.<Void>builder()
                            .success(false)
                            .message("can not be deleted->" + e.getMessage())
                            .data(null)
                            .build());

        }
    }
}
