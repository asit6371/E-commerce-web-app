package com.ecommerce.service;

import com.ecommerce.dto.ProductRequestDto;
import com.ecommerce.dto.ProductResponseDto;
import com.ecommerce.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto dto);
    ProductResponseDto updateProduct(Long productId, ProductRequestDto dto) throws ResourceNotFoundException;
    void deleteProduct(Long productId) throws ResourceNotFoundException;
    ProductResponseDto updateStock(Long productId, long newStock) throws ResourceNotFoundException;
    ProductResponseDto getProductById(Long productId) throws ResourceNotFoundException;
    Page<ProductResponseDto> getAllProducts(int page, int size, String sortBy, String sortDir);
    List<ProductResponseDto> searchProducts(String keyword);
    List<ProductResponseDto> filterProducts(String category, String brand, Double minPrice, Double maxPrice);
}
