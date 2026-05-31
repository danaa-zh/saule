package com.example.onlineshop.repository;

import com.example.onlineshop.model.Category;
import com.example.onlineshop.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

/**
 * Handles all product and category data from Firestore.
 * Follows SRP: data access only. No UI logic.
 */
public class ProductRepository {

    private static final String COLLECTION_PRODUCTS   = "products";
    private static final String COLLECTION_CATEGORIES = "categories";
    private static final int    PAGE_SIZE              = 20;

    private final FirebaseFirestore db;

    public ProductRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    // ─── Products ──────────────────────────────────────────────────────────────

    /** Fetch featured/popular products for the home screen. */
    public void getFeaturedProducts(FirebaseCallback<List<Product>> callback) {
        db.collection(COLLECTION_PRODUCTS)
                .whereEqualTo("featured", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)
                .get()
                .addOnSuccessListener(snapshots ->
                        callback.onSuccess(snapshots.toObjects(Product.class)))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /** Fetch all products for a specific category. */
    public void getProductsByCategory(String categoryId,
                                      FirebaseCallback<List<Product>> callback) {
        db.collection(COLLECTION_PRODUCTS)
                .whereEqualTo("categoryId", categoryId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)
                .get()
                .addOnSuccessListener(snapshots ->
                        callback.onSuccess(snapshots.toObjects(Product.class)))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /** Fetch a single product by ID (for product detail screen). */
    public void getProductById(String productId,
                               FirebaseCallback<Product> callback) {
        db.collection(COLLECTION_PRODUCTS)
                .document(productId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        callback.onSuccess(snapshot.toObject(Product.class));
                    } else {
                        callback.onFailure("Product not found.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /** Search products by name (prefix match). */
    public void searchProducts(String query, FirebaseCallback<List<Product>> callback) {
        String queryLower = query.toLowerCase().trim();
        // Firestore doesn't support full-text search natively.
        // Range query on name field for prefix matching.
        db.collection(COLLECTION_PRODUCTS)
                .orderBy("name")
                .startAt(queryLower)
                .endAt(queryLower + "\uf8ff")
                .limit(PAGE_SIZE)
                .get()
                .addOnSuccessListener(snapshots ->
                        callback.onSuccess(snapshots.toObjects(Product.class)))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ─── Categories ────────────────────────────────────────────────────────────

    /** Fetch all categories ordered by sortOrder. */
    public void getCategories(FirebaseCallback<List<Category>> callback) {
        db.collection(COLLECTION_CATEGORIES)
                .orderBy("sortOrder", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshots ->
                        callback.onSuccess(snapshots.toObjects(Category.class)))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}