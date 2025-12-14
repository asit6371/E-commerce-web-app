package com.ecommerce.controller;

import com.ecommerce.dto.ProductRequestDto;
import com.ecommerce.dto.ProductResponseDto;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;




    @PostMapping("/api/admin/products")
    public ResponseEntity<ProductResponseDto> createProduct(
            @RequestBody ProductRequestDto dto) {
        ProductResponseDto created = productService.createProduct(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);

    }

    @PutMapping("/api/admin/products/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDto dto) throws ResourceNotFoundException {

        ProductResponseDto updated = productService.updateProduct(id, dto);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/admin/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) throws ResourceNotFoundException {
        productService.deleteProduct(id);

        return ResponseEntity.ok("Product deleted Successfully");
    }

    @PatchMapping("/api/admin/products/{id}/stock")
    public ResponseEntity<ProductResponseDto> updateStock(
            @PathVariable Long id,
            @RequestParam long stock) throws ResourceNotFoundException {

        ProductResponseDto updated = productService.updateStock(id, stock);

        return ResponseEntity.ok(updated);
    }



    @GetMapping("/api/products/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) throws ResourceNotFoundException {
        ProductResponseDto product = productService.getProductById(id);

        return ResponseEntity.ok(product);
    }


    @GetMapping("/api/products")
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<ProductResponseDto> result =
                productService.getAllProducts(page, size, sortBy, sortDir);


        return ResponseEntity.ok(result);
    }


    @GetMapping("/api/products/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(
            @RequestParam String keyword) {

        List<ProductResponseDto> result = productService.searchProducts(keyword);

        return ResponseEntity.ok(result);
    }


    @GetMapping("/api/products/filter")
    public ResponseEntity<List<ProductResponseDto>> filterProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        List<ProductResponseDto> result = productService.filterProducts(category, brand, minPrice, maxPrice);

        return ResponseEntity.ok(result);
    }

}