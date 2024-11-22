package com.example.ecommerceapp.service;

import com.example.ecommerceapp.model.entities.Product;
import com.example.ecommerceapp.repositories.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Cacheable(value = "productList")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Gets product details by ID and caches the result.
     *
     * @param id Product ID
     * @return An Optional containing the product details if found
     */
    @Cacheable(value = "productList", key = "#id")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Adds a new product and clears the cache.
     *
     * @param product Product to be added
     * @return The created product
     */
    @CacheEvict(value = "productList", allEntries = true)
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Updates an existing product and clears the cache.
     *
     * @param id Product ID
     * @param productDetails Updated product details
     * @return The updated product
     */
    @CacheEvict(value = "productList", allEntries = true)
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        return productRepository.save(product);
    }

    /**
     * Deletes a product by ID and clears the cache.
     *
     * @param id Product ID to be deleted
     */
    @CacheEvict(value = "productList", allEntries = true)
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
