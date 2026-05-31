package com.example.onlineshop.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Objects;

public class Category {

    @DocumentId
    private String id;
    private String name;
    private String iconUrl;
    private int productCount;
    private int sortOrder;

    // Required for Firestore
    public Category() {}

    public Category(String id, String name, int sortOrder) {
        this.id = id;
        this.name = name;
        this.sortOrder = sortOrder;
        this.productCount = 0;
        this.iconUrl = "";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public int getProductCount() { return productCount; }
    public void setProductCount(int productCount) { this.productCount = productCount; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
