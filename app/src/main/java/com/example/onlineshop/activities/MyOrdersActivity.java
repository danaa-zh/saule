package com.example.onlineshop.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlineshop.databinding.ActivityMyOrdersBinding;
import com.example.onlineshop.model.CartItem;
import com.example.onlineshop.repository.CartRepository;
import com.example.onlineshop.repository.FirebaseCallback;

import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private ActivityMyOrdersBinding binding;
    private CartRepository cartRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartRepository = new CartRepository();

        binding.backBtn.setOnClickListener(v -> finish());
        loadOrders();
    }

    private void loadOrders() {
        binding.progressBar.setVisibility(View.VISIBLE);

        cartRepository.getCartItems(new FirebaseCallback<List<CartItem>>() {
            @Override
            public void onSuccess(List<CartItem> result) {
                binding.progressBar.setVisibility(View.GONE);
                if (result.isEmpty()) {
                    binding.emptyState.setVisibility(View.VISIBLE);
                    binding.ordersRv.setVisibility(View.GONE);
                } else {
                    binding.emptyState.setVisibility(View.GONE);
                    binding.ordersRv.setVisibility(View.VISIBLE);
                    binding.ordersRv.setLayoutManager(new LinearLayoutManager(MyOrdersActivity.this));
                    binding.ordersRv.setAdapter(new OrdersAdapter(result));
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                binding.progressBar.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.ordersRv.setVisibility(View.GONE);
            }
        });
    }

    private static class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.VH> {

        private final List<CartItem> items;

        OrdersAdapter(List<CartItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            CartItem item = items.get(position);
            holder.text1.setText(item.getProductName());
            holder.text2.setText(String.format("Qty: %d  |  Color: %s",
                    item.getQuantity(), item.getSelectedColor()));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView text1, text2;
            VH(View v) {
                super(v);
                text1 = v.findViewById(android.R.id.text1);
                text2 = v.findViewById(android.R.id.text2);
            }
        }
    }
}