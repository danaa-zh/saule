package com.example.onlineshop.repository;

public interface FirebaseCallback<T> {
    void onSuccess(T result);
    void onFailure(String errorMessage);
}
