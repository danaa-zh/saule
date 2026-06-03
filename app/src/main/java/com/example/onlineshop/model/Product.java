package com.example.onlineshop.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product {

    @DocumentId
    private String id;
    private String name;
    private String description;
    private long price;
    private String categoryId;
    private String categoryName;
    private List<String> imageUrls;
    private List<String> colors;
    private double rating;
    private long reviewCount;
    private long stockCount;
    @PropertyName("featured")
    private boolean featured;
    private Timestamp createdAt;

    public Product() {
        imageUrls = new ArrayList<>();
        colors = new ArrayList<>();
    }

    public Product(String name, String description, long price,
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
        this.featured = false;
        this.createdAt = Timestamp.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

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

    public long getReviewCount() { return reviewCount; }
    public void setReviewCount(long reviewCount) { this.reviewCount = reviewCount; }

    public long getStockCount() { return stockCount; }
    public void setStockCount(long stockCount) { this.stockCount = stockCount; }

    @PropertyName("featured")
    public boolean isFeatured() { return featured; }

    @PropertyName("featured")
    public void setFeatured(boolean featured) { this.featured = featured; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Exclude
    public String getFirstImageUrl() {
        if (imageUrls != null && !imageUrls.isEmpty()) return imageUrls.get(0);
        return "";
    }

    @Exclude
    public String getFormattedPrice() {
        return String.format("%,.0f ₸", (double) price);
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