package com.example.onlineshop.repository;

import com.example.onlineshop.model.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles all Firebase Authentication and user profile operations.
 * Activities never touch FirebaseAuth directly — they use this repository.
 * Follows SRP: auth logic only, no UI, no navigation.
 */
public class AuthRepository {

    private static final String COLLECTION_USERS = "users";

    private final FirebaseAuth      auth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.db   = FirebaseFirestore.getInstance();
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

    // ─── Sign In (Email/Password) ───────────────────────────────────────────────

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

    // ─── Sign In (Google) ──────────────────────────────────────────────────────

    /**
     * Completes Google Sign-In by exchanging the Google ID token for a Firebase credential.
     * Call this from SignInActivity after a successful GoogleSignIn intent result.
     *
     * @param idToken  The ID token from GoogleSignInAccount.getIdToken()
     * @param callback Returns the User profile on success
     */
    public void firebaseAuthWithGoogle(String idToken, FirebaseCallback<User> callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onFailure("Google sign-in failed. Please try again.");
                        return;
                    }

                    // Check if this is a new user — if so, create their Firestore profile
                    boolean isNewUser = authResult.getAdditionalUserInfo() != null
                            && authResult.getAdditionalUserInfo().isNewUser();

                    if (isNewUser) {
                        String name  = firebaseUser.getDisplayName() != null
                                ? firebaseUser.getDisplayName() : "User";
                        String email = firebaseUser.getEmail() != null
                                ? firebaseUser.getEmail() : "";
                        User user = new User(firebaseUser.getUid(), name, email);

                        // Store photo URL from Google account
                        if (firebaseUser.getPhotoUrl() != null) {
                            user.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
                        }

                        saveUserToFirestore(user, callback);
                    } else {
                        // Returning user — fetch existing profile
                        fetchUserFromFirestore(firebaseUser.getUid(), callback);
                    }
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
                        callback.onSuccess(snapshot.toObject(User.class));
                    } else {
                        // Signed in but no Firestore profile — create a basic one
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