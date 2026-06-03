package com.example.onlineshop.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlineshop.databinding.ActivityCartBinding;
import com.example.onlineshop.model.CartItem;
import com.example.onlineshop.repository.CartRepository;
import com.example.onlineshop.repository.FirebaseCallback;
import com.example.onlineshop.ui.adapter.CartAdapter;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartRepository cartRepository;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartRepository = new CartRepository();

        setupAdapter();
        setupClickListeners();
        loadCart();
    }

    private void setupAdapter() {
        cartAdapter = new CartAdapter(new CartAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
                updateItemQuantity(item, newQuantity);
            }

            @Override
            public void onDeleteItem(CartItem item) {
                deleteItem(item);
            }
        });

        binding.cartRv.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.checkoutBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Proceeding to checkout…", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCart() {
        setLoading(true);

        cartRepository.getCartItems(new FirebaseCallback<List<CartItem>>() {
            @Override
            public void onSuccess(List<CartItem> result) {
                setLoading(false);
                if (result.isEmpty()) {
                    showEmptyState(true);
                } else {
                    showEmptyState(false);
                    cartAdapter.submitList(result);
                    updateTotal(result);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                showEmptyState(true);
            }
        });
    }

    private void updateItemQuantity(CartItem item, int newQuantity) {
        cartRepository.updateQuantity(item.getId(), newQuantity, new FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                List<CartItem> updated = new ArrayList<>(cartAdapter.getCurrentList());
                for (CartItem ci : updated) {
                    if (ci.getId().equals(item.getId())) { ci.setQuantity(newQuantity); break; }
                }
                cartAdapter.submitList(updated);
                updateTotal(updated);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteItem(CartItem item) {
        cartRepository.removeFromCart(item.getId(), new FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loadCart();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotal(List<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        binding.totalPriceTv.setText(String.format("%,.0f ₸", total));
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            binding.cartRv.setVisibility(View.GONE);
            binding.emptyState.setVisibility(View.GONE);
            binding.orderSummaryCard.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(boolean empty) {
        binding.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.cartRv.setVisibility(empty ? View.GONE : View.VISIBLE);
        binding.orderSummaryCard.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
