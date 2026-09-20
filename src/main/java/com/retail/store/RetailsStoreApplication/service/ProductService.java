package com.retail.store.RetailsStoreApplication.service;

import com.retail.store.RetailsStoreApplication.dto.ProductDto;
import com.retail.store.RetailsStoreApplication.dto.ProductResponse;
import com.retail.store.RetailsStoreApplication.model.Product;
import com.retail.store.RetailsStoreApplication.repository.ProductRepository;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ConcurrentMap;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final CacheManager cacheManager;

    private final RestTemplate restTemplate;

    public ProductService(ProductRepository productRepository, CacheManager cacheManager, RestTemplate restTemplate) {
        this.productRepository = productRepository;
        this.cacheManager = cacheManager;
        this.restTemplate = restTemplate;
    }


    public ProductResponse saveProduct(ProductDto productDto) {
        ProductResponse productResponse = new ProductResponse();
        try {

            Product build = Product.builder()
                    .productName(productDto.getProductName())
                    .productDescription(productDto.getProductDescription())
                    .productPrice(productDto.getProductPrice())
                    .build();
            productRepository.save(build);


            // Simulating a successful save operation
            productResponse.setProductId(build.getProductId()); // This would be the ID of the saved product
            productResponse.setStatus("Success");
            productResponse.setMessage("Product saved successfully.");
        } catch (Exception e) {
            productResponse.setStatus("Error");
            productResponse.setMessage("Failed to save product: " + e.getMessage());
        }
        return productResponse;
    }

    @Cacheable (cacheNames = "products", key = "#productId")
    public Product getByProductId(Long productId) {
        return productRepository.findByProductId(Long.parseLong(String.valueOf(productId)));
    }

    @CachePut(cacheNames = "products", key = "#productDto.productId")
    public Product updateProduct(Product productDto) {

        Product existingProduct =
                productRepository.findByProductId(productDto.getProductId());

        if (existingProduct == null) {
            throw new RuntimeException("Product not found");
        }

        existingProduct.setProductName(productDto.getProductName());
        existingProduct.setProductDescription(productDto.getProductDescription());
        existingProduct.setProductPrice(productDto.getProductPrice());

        return productRepository.save(existingProduct);
    }

    public void getCacheProducts(String cacheName) {

        Cache products = cacheManager.getCache(cacheName);

        if (products != null) {
            System.out.println("Cached Products:");

            ConcurrentMap<Object, Object> nativeCache =
                    (ConcurrentMap<Object, Object>) products.getNativeCache();

            nativeCache.forEach((key, value) ->
                    System.out.println("Key: " + key + ", Value: " + value)
            );

        } else {
            System.out.println("No cached products found.");
        }
    }

    @Retry(name = "productService",
            fallbackMethod = "productServiceFallback")
    public String connectEmployeeService(Long productId) {

        System.out.println("Connecting to Employee Service");
        return restTemplate.getForObject(
                "http://localhost:8090/" + productId,
                String.class
        );
    }

    public String productServiceFallback(Long productId, Exception ex) {

        return "Product service is currently unavailable";
    }
}
