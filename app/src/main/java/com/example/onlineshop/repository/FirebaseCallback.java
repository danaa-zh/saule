package com.example.onlineshop.repository;

/**
 * Generic callback for async Firebase operations.
 * Eliminates duplicate listener boilerplate across all repositories.
 *
 * @param <T> The data type returned on success
 */
public interface FirebaseCallback<T> {
    void onSuccess(T result);
    void onFailure(String errorMessage);
}
