package com.example.onlineshop.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.Objects;

public class CartItem {

    @DocumentId
    private String id;
    private String productId;
    private String productName;
    private String productImageUrl;
    private double productPrice;
    private String selectedColor;
    private int quantity;

    // Required for Firestore
    public CartItem() {}

    public CartItem(Product product, String selectedColor, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.productImageUrl = product.getFirstImageUrl();
        this.productPrice = product.getPrice();
        this.selectedColor = selectedColor;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public String getSelectedColor() { return selectedColor; }
    public void setSelectedColor(String selectedColor) { this.selectedColor = selectedColor; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Exclude
    public double getTotalPrice() { return productPrice * quantity; }

    @Exclude
    public String getFormattedTotalPrice() {
        return String.format("%,.0f ₸", getTotalPrice());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(id, cartItem.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}

