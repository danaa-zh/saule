package com.example.onlineshop.repository;

import com.example.onlineshop.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles all Firebase Authentication and user profile operations.
 * Activities never touch FirebaseAuth directly — they use this repository.
 * Follows SRP: auth logic only, no UI, no navigation.
 */
public class AuthRepository {

    private static final String COLLECTION_USERS = "users";

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    // ─── Auth State ────────────────────────────────────────────────────────────

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return auth.getCurrentUser();
    }

    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // ─── Sign Up ───────────────────────────────────────────────────────────────

    public void signUp(String fullName, String email, String password,
                       FirebaseCallback<User> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onFailure("Registration failed. Please try again.");
                        return;
                    }
                    User user = new User(firebaseUser.getUid(), fullName, email);
                    saveUserToFirestore(user, callback);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ─── Sign In ───────────────────────────────────────────────────────────────

    public void signIn(String email, String password,
                       FirebaseCallback<User> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onFailure("Sign in failed. Please try again.");
                        return;
                    }
                    fetchUserFromFirestore(firebaseUser.getUid(), callback);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ─── Sign Out ──────────────────────────────────────────────────────────────

    public void signOut() {
        auth.signOut();
    }

    // ─── Get Current User Profile ──────────────────────────────────────────────

    public void fetchCurrentUser(FirebaseCallback<User> callback) {
        String uid = getCurrentUserId();
        if (uid == null) {
            callback.onFailure("No user logged in.");
            return;
        }
        fetchUserFromFirestore(uid, callback);
    }

    // ─── Private Helpers ───────────────────────────────────────────────────────

    private void saveUserToFirestore(User user, FirebaseCallback<User> callback) {
        db.collection(COLLECTION_USERS)
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(unused -> callback.onSuccess(user))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private void fetchUserFromFirestore(String uid, FirebaseCallback<User> callback) {
        db.collection(COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        User user = snapshot.toObject(User.class);
                        callback.onSuccess(user);
                    } else {
                        // User signed in but profile missing — create basic profile
                        FirebaseUser fbUser = auth.getCurrentUser();
                        if (fbUser != null) {
                            String name = fbUser.getDisplayName() != null
                                    ? fbUser.getDisplayName() : "User";
                            User user = new User(uid, name,
                                    fbUser.getEmail() != null ? fbUser.getEmail() : "");
                            saveUserToFirestore(user, callback);
                        } else {
                            callback.onFailure("User profile not found.");
                        }
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
