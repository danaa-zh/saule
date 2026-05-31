package com.example.onlineshop.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ItemCategoryHomeBinding;
import com.example.onlineshop.model.Category;

/**
 * Horizontal category icon row adapter for the Home screen.
 * Uses ListAdapter + DiffUtil. Follows SRP: display only.
 */
public class CategoryHomeAdapter extends ListAdapter<Category, CategoryHomeAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private final OnCategoryClickListener listener;

    public CategoryHomeAdapter(OnCategoryClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Category>() {
                @Override
                public boolean areItemsTheSame(@NonNull Category o, @NonNull Category n) {
                    return o.getId() != null && o.getId().equals(n.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Category o, @NonNull Category n) {
                    return o.equals(n);
                }
            };

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryHomeBinding binding = ItemCategoryHomeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final ItemCategoryHomeBinding binding;

        CategoryViewHolder(ItemCategoryHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Category category) {
            binding.categoryName.setText(category.getName());

            if (category.getIconUrl() != null && !category.getIconUrl().isEmpty()) {
                Glide.with(binding.categoryIcon.getContext())
                        .load(category.getIconUrl())
                        .placeholder(R.drawable.ic_catalog)
                        .into(binding.categoryIcon);
            } else {
                binding.categoryIcon.setImageResource(R.drawable.ic_catalog);
            }

            binding.getRoot().setOnClickListener(v -> listener.onCategoryClick(category));
        }
    }
}
