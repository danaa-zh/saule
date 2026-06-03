package com.example.onlineshop.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ItemCartBinding;
import com.example.onlineshop.model.CartItem;

public class CartAdapter extends ListAdapter<CartItem, CartAdapter.ViewHolder> {

    public interface CartItemListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onDeleteItem(CartItem item);
    }

    private final CartItemListener listener;

    public CartAdapter(CartItemListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding binding = ItemCartBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemCartBinding binding;

        ViewHolder(ItemCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CartItem item, CartItemListener listener) {
            binding.productName.setText(item.getProductName());
            binding.priceTv.setText(item.getFormattedTotalPrice());
            binding.quantityTv.setText(String.valueOf(item.getQuantity()));

            if (item.getSelectedColor() != null && !item.getSelectedColor().isEmpty()) {
                binding.colorTv.setText("Color: " + item.getSelectedColor());
            } else {
                binding.colorTv.setText("");
            }

            Glide.with(binding.getRoot().getContext())
                    .load(item.getProductImageUrl())
                    .placeholder(R.drawable.ic_catalog)
                    .error(R.drawable.ic_catalog)
                    .centerCrop()
                    .into(binding.productImage);

            binding.increaseBtn.setOnClickListener(v -> {
                int newQty = item.getQuantity() + 1;
                binding.quantityTv.setText(String.valueOf(newQty));
                listener.onQuantityChanged(item, newQty);
            });

            binding.decreaseBtn.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    int newQty = item.getQuantity() - 1;
                    binding.quantityTv.setText(String.valueOf(newQty));
                    listener.onQuantityChanged(item, newQty);
                } else {
                    listener.onDeleteItem(item);
                }
            });

            binding.deleteBtn.setOnClickListener(v -> listener.onDeleteItem(item));
        }
    }

    private static final DiffUtil.ItemCallback<CartItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CartItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull CartItem a, @NonNull CartItem b) {
                    return a.getId() != null && a.getId().equals(b.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull CartItem a, @NonNull CartItem b) {
                    return a.equals(b)
                            && a.getQuantity() == b.getQuantity();
                }
            };
}
