package com.example.onlineshop.Domain;

import java.util.ArrayList;

public class ItemsModel {
    private String title;
    private String description;
    private double price;
    private double rating;
    private int numberinCart;
    private ArrayList<String> picUrl;
    private String categoryId;

    public ItemsModel() {}

    public ItemsModel(String title, String description, double price,
                      double rating, int numberinCart,
                      ArrayList<String> picUrl, String categoryId) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.numberinCart = numberinCart;
        this.picUrl = picUrl;
        this.categoryId = categoryId;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getNumberinCart() { return numberinCart; }
    public void setNumberinCart(int numberinCart) { this.numberinCart = numberinCart; }

    public ArrayList<String> getPicUrl() { return picUrl; }
    public void setPicUrl(ArrayList<String> picUrl) { this.picUrl = picUrl; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
}