package com.guvi.dto;

import com.guvi.model.ProductStatus;
import jakarta.validation.constraints.*;

import java.util.Set;

public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;
    private String description;
    @NotNull
    @Positive(message = "Price must be greater than 0")
    private double price;
    @NotNull
    @PositiveOrZero(message = "Stock cannot be negative")
    private double stockQuantity;
    @NotEmpty(message = "At least one category is required")
    private Set<String> categories;
    @NotNull
    private ProductStatus status;

    public CreateProductRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
