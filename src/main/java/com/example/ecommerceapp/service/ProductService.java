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
     * ID'ye göre ürün bilgilerini getirir ve önbelleğe alır.
     *
     * @param id Ürün ID'si
     * @return Belirli bir ürünü içeren Optional
     */
    @Cacheable(value = "productList", key = "#id")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Yeni ürün ekler ve önbelleği temizler.
     *
     * @param product Eklenecek ürün
     * @return Oluşturulan ürün
     */
    @CacheEvict(value = "productList", allEntries = true)
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Ürünü günceller ve önbelleği temizler.
     *
     * @param id             Ürün ID'si
     * @param productDetails Güncellenmiş ürün detayları
     * @return Güncellenmiş ürün
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
     * Ürünü siler ve önbelleği temizler.
     *
     * @param id Silinecek ürün ID'si
     */
    @CacheEvict(value = "productList", allEntries = true)
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
