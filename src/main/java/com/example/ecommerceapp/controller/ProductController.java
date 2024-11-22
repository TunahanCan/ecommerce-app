package com.example.ecommerceapp.controller;

import com.example.ecommerceapp.model.dto.ApiResponseDTO;
import com.example.ecommerceapp.model.dto.ProductRequestDTO;
import com.example.ecommerceapp.model.entities.Product;
import com.example.ecommerceapp.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
     * Get all products.
     *
     * @return A list of all products.
     */
    @Operation(summary = "Retrieve all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product list retrieved successfully")
    })
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
     * Get product by ID.
     *
     * @param id Product ID.
     * @return Product details or not found message.
     */
    @Operation(summary = "Get product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
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
     * Create a new product.
     *
     * @param productRequest The details of the product to be created.
     * @return The created product.
     */
    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "500", description = "Failed to create product")
    })
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
     * Update an existing product.
     *
     * @param id             Product ID.
     * @param productRequest Updated product details.
     * @return The updated product or a not found message.
     */
    @Operation(summary = "Update an existing product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
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
     * Delete a product by ID.
     *
     * @param id Product ID to be deleted.
     * @return Success message or error message if not deleted.
     */
    @Operation(summary = "Delete a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Failed to delete product")
    })
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
