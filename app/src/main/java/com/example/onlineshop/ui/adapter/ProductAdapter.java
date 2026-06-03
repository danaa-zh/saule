package com.example.onlineshop.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ItemProductBinding;
import com.example.onlineshop.model.Product;

import java.util.HashSet;
import java.util.Set;

public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onFavoriteClick(Product product);
    }

    private final OnProductClickListener listener;
    private final Set<String> favoriteIds = new HashSet<>();

    public ProductAdapter(OnProductClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    public void setFavoriteIds(Set<String> ids) {
        favoriteIds.clear();
        if (ids != null) favoriteIds.addAll(ids);
        notifyDataSetChanged();
    }

    public void toggleFavoriteId(String productId) {
        if (favoriteIds.contains(productId)) {
            favoriteIds.remove(productId);
        } else {
            favoriteIds.add(productId);
        }
        int pos = getPositionById(productId);
        if (pos >= 0) notifyItemChanged(pos);
    }

    private int getPositionById(String id) {
        for (int i = 0; i < getCurrentList().size(); i++) {
            if (id.equals(getCurrentList().get(i).getId())) return i;
        }
        return -1;
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

            Glide.with(binding.productImage.getContext())
                    .load(product.getFirstImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_catalog)
                            .error(R.drawable.ic_catalog)
                            .transform(new RoundedCorners(24)))
                    .into(binding.productImage);

            boolean isFav = favoriteIds.contains(product.getId());
            binding.favoriteBtn.setImageResource(
                    isFav ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            binding.favoriteBtn.setColorFilter(
                    ContextCompat.getColor(binding.getRoot().getContext(),
                            isFav ? R.color.purple : R.color.textSecondary));

            binding.getRoot().setOnClickListener(v -> listener.onProductClick(product));
            binding.favoriteBtn.setOnClickListener(v -> {
                listener.onFavoriteClick(product);
                toggleFavoriteId(product.getId());
            });
        }
    }
}