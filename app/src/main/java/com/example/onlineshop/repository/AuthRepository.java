package com.example.onlineshop.repository;

import com.example.onlineshop.model.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthRepository {

    private static final String COLLECTION_USERS = "users";

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.db   = FirebaseFirestore.getInstance();
    }

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

    public void firebaseAuthWithGoogle(String idToken, FirebaseCallback<User> callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onFailure("Google sign-in failed. Please try again.");
                        return;
                    }

                    boolean isNewUser = authResult.getAdditionalUserInfo() != null
                            && authResult.getAdditionalUserInfo().isNewUser();

                    if (isNewUser) {
                        String name  = firebaseUser.getDisplayName() != null
                                ? firebaseUser.getDisplayName() : "User";
                        String email = firebaseUser.getEmail() != null
                                ? firebaseUser.getEmail() : "";
                        User user = new User(firebaseUser.getUid(), name, email);

                        if (firebaseUser.getPhotoUrl() != null) {
                            user.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
                        }

                        saveUserToFirestore(user, callback);
                    } else {
                        fetchUserFromFirestore(firebaseUser.getUid(), callback);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void signOut() {
        auth.signOut();
    }

    public void fetchCurrentUser(FirebaseCallback<User> callback) {
        String uid = getCurrentUserId();
        if (uid == null) {
            callback.onFailure("No user logged in.");
            return;
        }
        fetchUserFromFirestore(uid, callback);
    }

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
                        callback.onSuccess(snapshot.toObject(User.class));
                    } else {
                        FirebaseUser fbUser = auth.getCurrentUser();
                        if (fbUser != null) {
                            String name = fbUser.getDisplayName() != null
                                    ? fbUser.getDisplayName() : "User";
                            String email = fbUser.getEmail() != null
                                    ? fbUser.getEmail() : "";
                            User user = new User(uid, name, email);
                            if (fbUser.getPhotoUrl() != null) {
                                user.setPhotoUrl(fbUser.getPhotoUrl().toString());
                            }
                            saveUserToFirestore(user, callback);
                        } else {
                            callback.onFailure("User profile not found.");
                        }
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
