package com.valliappanmart.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

public class Product {
    private Long id;
    private Long sellerId;
    private Long categoryId;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private int stockQuantity;
    private boolean isActive;
    private String mainImage;
    private String subImage1;
    private String subImage2;
    private BigDecimal rating;
    private int ratingCount;
    private String brand;
    private Timestamp createdAt;

    // Joined fields
    private String categoryName;
    private String storeName;

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getSubImage1() {
        return subImage1;
    }

    public void setSubImage1(String subImage1) {
        this.subImage1 = subImage1;
    }

    public String getSubImage2() {
        return subImage2;
    }

    public void setSubImage2(String subImage2) {
        this.subImage2 = subImage2;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    // Helper calculations
    public int getDiscountPercentage() {
        if (originalPrice != null && price != null && originalPrice.compareTo(price) > 0 && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = originalPrice.subtract(price);
            return diff.multiply(BigDecimal.valueOf(100)).divide(originalPrice, 0, RoundingMode.HALF_UP).intValue();
        }
        return 0;
    }

    public String getStockBadgeText() {
        if (stockQuantity <= 0) {
            return "Currently Out of Stock";
        } else if (stockQuantity <= 5) {
            return "Only " + stockQuantity + " Left in Stock - Order Soon!";
        } else {
            return "In Stock";
        }
    }

    public String getStockBadgeClass() {
        if (stockQuantity <= 0) {
            return "badge-out-of-stock";
        } else if (stockQuantity <= 5) {
            return "badge-low-stock";
        } else {
            return "badge-in-stock";
        }
    }
}
