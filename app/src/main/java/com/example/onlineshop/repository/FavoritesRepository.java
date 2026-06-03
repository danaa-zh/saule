package com.example.onlineshop.repository;

import com.example.onlineshop.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritesRepository {

    private static final String COLLECTION_USERS     = "users";
    private static final String COLLECTION_FAVORITES = "favorites";

    private final FirebaseFirestore db;
    private final FirebaseAuth      auth;

    public FavoritesRepository() {
        this.db   = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void getFavorites(FirebaseCallback<List<Product>> callback) {
        String uid = getCurrentUid(callback);
        if (uid == null) return;

        db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(COLLECTION_FAVORITES)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Product p = documentToProduct(doc);
                        if (p != null) products.add(p);
                    }
                    callback.onSuccess(products);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void isFavorite(String productId, FirebaseCallback<Boolean> callback) {
        String uid = getCurrentUid(callback);
        if (uid == null) return;

        db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(COLLECTION_FAVORITES)
                .document(productId)
                .get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(snapshot.exists()))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void addToFavorites(Product product, FirebaseCallback<Void> callback) {
        String uid = getCurrentUid(callback);
        if (uid == null) return;

        db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(COLLECTION_FAVORITES)
                .document(product.getId())
                .set(productToMap(product))
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void removeFromFavorites(String productId, FirebaseCallback<Void> callback) {
        String uid = getCurrentUid(callback);
        if (uid == null) return;

        db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(COLLECTION_FAVORITES)
                .document(productId)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void toggleFavorite(Product product, FirebaseCallback<Boolean> callback) {
        isFavorite(product.getId(), new FirebaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isFav) {
                if (isFav) {
                    removeFromFavorites(product.getId(), new FirebaseCallback<Void>() {
                        @Override public void onSuccess(Void v) { callback.onSuccess(false); }
                        @Override public void onFailure(String e) { callback.onFailure(e); }
                    });
                } else {
                    addToFavorites(product, new FirebaseCallback<Void>() {
                        @Override public void onSuccess(Void v) { callback.onSuccess(true); }
                        @Override public void onFailure(String e) { callback.onFailure(e); }
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }

    private Map<String, Object> productToMap(Product product) {
        Map<String, Object> data = new HashMap<>();
        data.put("productId",   product.getId());
        data.put("name",        product.getName());
        data.put("imageUrl",    product.getFirstImageUrl());
        data.put("price",       product.getPrice());
        data.put("categoryId",  product.getCategoryId());
        data.put("rating",      product.getRating());
        data.put("reviewCount", product.getReviewCount());
        data.put("addedAt",     System.currentTimeMillis());
        return data;
    }

    private Product documentToProduct(DocumentSnapshot doc) {
        try {
            Product p = new Product();
            p.setId(doc.getId());

            String name = doc.getString("name");
            p.setName(name != null ? name : "");

            String imageUrl = doc.getString("imageUrl");
            if (imageUrl != null) {
                List<String> images = new ArrayList<>();
                images.add(imageUrl);
                p.setImageUrls(images);
            }

            Long price = doc.getLong("price");
            p.setPrice(price != null ? price : 0L);

            String categoryId = doc.getString("categoryId");
            p.setCategoryId(categoryId != null ? categoryId : "");

            Double rating = doc.getDouble("rating");
            p.setRating(rating != null ? rating : 0.0);

            Long reviewCount = doc.getLong("reviewCount");
            p.setReviewCount(reviewCount != null ? reviewCount : 0L);

            return p;
        } catch (Exception e) {
            return null;
        }
    }

    private <T> String getCurrentUid(FirebaseCallback<T> callback) {
        String uid = auth.getCurrentUser() != null
                ? auth.getCurrentUser().getUid() : null;
        if (uid == null) callback.onFailure("User not authenticated.");
        return uid;
    }
}