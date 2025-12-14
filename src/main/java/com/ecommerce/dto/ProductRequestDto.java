package com.ecommerce.dto;

public class ProductRequestDto {

    private String productName;
    private String brand;
    private String description;
    private double price;
    private String category;
    private long stock;

    public ProductRequestDto() {}

    public ProductRequestDto(String productName, String brand, String description,
                             double price, String category, long stock) {
        this.productName = productName;
        this.brand = brand;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stock = stock;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getStock() {
        return stock;
    }

    public void setStock(long stock) {
        this.stock = stock;
    }
}
