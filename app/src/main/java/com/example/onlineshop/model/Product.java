package com.example.onlineshop.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product {

    @DocumentId
    private String id;
    private String name;
    private String description;
    private double price;
    private String categoryId;
    private String categoryName;
    private List<String> imageUrls;
    private List<String> colors;
    private double rating;
    private int reviewCount;
    private int stockCount;
    private boolean isFeatured;
    private long createdAt;

    // Required empty constructor for Firestore
    public Product() {
        imageUrls = new ArrayList<>();
        colors = new ArrayList<>();
    }

    public Product(String name, String description, double price,
                   String categoryId, String categoryName) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imageUrls = new ArrayList<>();
        this.colors = new ArrayList<>();
        this.rating = 0.0;
        this.reviewCount = 0;
        this.stockCount = 0;
        this.isFeatured = false;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public List<String> getColors() { return colors; }
    public void setColors(List<String> colors) { this.colors = colors; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public int getStockCount() { return stockCount; }
    public void setStockCount(int stockCount) { this.stockCount = stockCount; }

    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    @Exclude
    public String getFirstImageUrl() {
        if (imageUrls != null && !imageUrls.isEmpty()) return imageUrls.get(0);
        return "";
    }

    @Exclude
    public String getFormattedPrice() {
        return String.format("%,.0f ₸", price);
    }

    @Exclude
    public boolean isInStock() { return stockCount > 0; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}

