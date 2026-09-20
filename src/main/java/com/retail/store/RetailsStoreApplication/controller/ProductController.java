package com.retail.store.RetailsStoreApplication.controller;

import com.retail.store.RetailsStoreApplication.dto.ProductDto;
import com.retail.store.RetailsStoreApplication.dto.ProductResponse;
import com.retail.store.RetailsStoreApplication.model.Product;
import com.retail.store.RetailsStoreApplication.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/addProduct")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductDto productDto) {
        ProductResponse productResponse = productService.saveProduct(productDto);
        return ok(productResponse);
    }

    @GetMapping("/getProduct")
    public ResponseEntity<Product> getProduct(@RequestParam Long productId) {
        Product byProductId = productService.getByProductId(productId);
        return ok(byProductId);
    }

    @GetMapping("/getSampleAnswer")
    public ResponseEntity<String> connectDownStream(@RequestParam Long productId) {
        String answer  = productService.connectEmployeeService(productId);
        return ok(answer);
    }

    @PutMapping("/updateProduct")
    public ResponseEntity<Product> updateProduct(@RequestBody Product productDto) {
        Product productResponse = productService.updateProduct(productDto);
        return ok(productResponse);
    }

    @GetMapping("/cacheData")
    public ResponseEntity<Product> cache(@RequestParam String cacheName) {
        productService.getCacheProducts(cacheName);
        return ok().build();
    }
}
