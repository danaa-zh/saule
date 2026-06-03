package com.example.onlineshop.repository;

import com.example.onlineshop.model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartRepository {

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_CART  = "cart";

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public CartRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void getCartItems(FirebaseCallback<List<CartItem>> callback) {
        String uid = getCurrentUid(callback);
        if (uid == null) return;

        db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(COLLECTION_CART)
                .get()
                .addOnSuccessListener(snapshots ->
                        callback.onSuccess(snapshots.toObjects(CartItem.class)))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void addToCart(CartItem item, FirebaseCallback<Void> callback) {
        String uid = getCurrentUid(callback);
        if (uid == null) return;

        db.collection(COLLECTION_USERS).document(uid)
                .collection(COLLECTION_CART)
                .document(item.getProductId())
                .set(item)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateQuantity(String cartItemId, int newQuantity,
                               FirebaseCallback<Void> callback) {
        String uid = getCurrentUid(callback);
        if (uid == null) return;

        db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(COLLECTION_CART)
                .document(cartItemId)
                .update("quantity", newQuantity)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void removeFromCart(String cartItemId, FirebaseCallback<Void> callback) {
        String uid = getCurrentUid(callback);
        if (uid == null) return;

        db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(COLLECTION_CART)
                .document(cartItemId)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private <T> String getCurrentUid(FirebaseCallback<T> callback) {
        String uid = auth.getCurrentUser() != null
                ? auth.getCurrentUser().getUid() : null;
        if (uid == null) {
            callback.onFailure("User not authenticated.");
        }
        return uid;
    }
}
