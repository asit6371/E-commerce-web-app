package com.ecommerce.service;

import com.ecommerce.dto.ProductRequestDto;
import com.ecommerce.dto.ProductResponseDto;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;


    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDto createProduct(ProductRequestDto dto) {
        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setBrand(dto.getBrand());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setStock(dto.getStock());
        product.setActive(true);


        Product saved = productRepository.save(product);
        return mapToResponseDto(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDto updateProduct(Long productId, ProductRequestDto dto) throws ResourceNotFoundException {
        Product product = getProductEntity(productId);

        product.setProductName(dto.getProductName());
        product.setBrand(product.getBrand());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setStock(dto.getStock());

        Product updated = productRepository.save(product);
        return mapToResponseDto(updated);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Long productId) throws ResourceNotFoundException {
        Product product = getProductEntity(productId);
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponseDto updateStock(Long productId, long newStock) throws ResourceNotFoundException {
        Product product = getProductEntity(productId);
        product.setStock(newStock);
        Product updated = productRepository.save(product);
        return mapToResponseDto(updated);
    }

    @Override
    public ProductResponseDto getProductById(Long productId) throws ResourceNotFoundException {
        Product product = getProductEntity(productId);

        if (!product.isActive() || product.getStock() <= 0) {
            throw new ResourceNotFoundException("Product not available");
        }
        return mapToResponseDto(product);
    }

    @Override
    public Page<ProductResponseDto> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponseDto> content = productPage.getContent().stream()
                .filter(p -> p.isActive() && p.getStock() > 0)
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());


        return new PageImpl<>(content, pageable, productPage.getTotalElements());
    }

    @Override
    public List<ProductResponseDto> searchProducts(String keyword) {
        List<Product> products = productRepository.
                findByIsActiveTrueAndProductNameContainingIgnoreCaseOrIsActiveTrueAndDescriptionContainingIgnoreCase(
                        keyword, keyword
                );


        return products.stream()
                .filter(p -> p.getStock() > 0)
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDto> filterProducts(String category, String brand, Double minPrice, Double maxPrice) {
        double min = (minPrice != null) ? minPrice : 0.0;
        double max = (maxPrice != null) ? maxPrice : Double.MAX_VALUE;

        List<Product> products;

        if (category != null && !category.isBlank()) {
            products = productRepository.findByIsActiveTrueAndCategoryIgnoreCaseAndPriceBetween(
                    category, min, max
            );
        } else {
            products = productRepository.findByIsActiveTrueAndPriceBetween(
                    min, max
            );
        }


        return products.stream()
                .filter(p -> p.getStock() > 0)
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private Product getProductEntity(Long productId) throws ResourceNotFoundException {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));
    }

    private ProductResponseDto mapToResponseDto(Product product) {

        ProductResponseDto dto = new ProductResponseDto();

        dto.setProductId(product.getProductId());
        dto.setBrand(product.getBrand());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setStock(product.getStock());
        dto.setActive(product.isActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        return dto;
    }

}
