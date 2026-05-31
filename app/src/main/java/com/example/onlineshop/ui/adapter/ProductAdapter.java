package com.example.onlineshop.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ItemProductBinding;
import com.example.onlineshop.model.Product;

/**
 * RecyclerView adapter for product grid/list.
 * Uses ListAdapter + DiffUtil for efficient updates.
 * Follows SRP: display logic only.
 */
public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onFavoriteClick(Product product);
    }

    private final OnProductClickListener listener;

    public ProductAdapter(OnProductClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Product>() {
                @Override
                public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                    return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                    return oldItem.equals(newItem) &&
                            oldItem.getPrice() == newItem.getPrice() &&
                            oldItem.getName().equals(newItem.getName());
                }
            };

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ItemProductBinding binding;

        ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Product product) {
            binding.productName.setText(product.getName());
            binding.priceTv.setText(product.getFormattedPrice());
            binding.ratingTv.setText(
                    String.format("%.1f (%d)", product.getRating(), product.getReviewCount()));

            // Load image with Glide
            Glide.with(binding.productImage.getContext())
                    .load(product.getFirstImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_catalog)
                            .error(R.drawable.ic_catalog)
                            .transform(new RoundedCorners(24)))
                    .into(binding.productImage);

            binding.getRoot().setOnClickListener(v -> listener.onProductClick(product));
            binding.favoriteBtn.setOnClickListener(v -> listener.onFavoriteClick(product));
        }
    }
}
