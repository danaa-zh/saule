package com.example.onlineshop.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ItemCategoryCatalogBinding;
import com.example.onlineshop.model.Category;

public class CategoryCatalogAdapter
        extends ListAdapter<Category, CategoryCatalogAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private final OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public CategoryCatalogAdapter(OnCategoryClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryCatalogBinding binding = ItemCategoryCatalogBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = getItem(position);
        boolean isSelected = (position == selectedPosition);
        holder.bind(category, isSelected);

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            listener.onCategoryClick(category);
        });
    }

    public void setSelectedPosition(int position) {
        int prev = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(prev);
        notifyItemChanged(selectedPosition);
    }

    public int findPositionById(String categoryId) {
        for (int i = 0; i < getCurrentList().size(); i++) {
            if (getItem(i).getId().equals(categoryId)) return i;
        }
        return -1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemCategoryCatalogBinding binding;

        ViewHolder(ItemCategoryCatalogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Category category, boolean isSelected) {
            binding.categoryName.setText(category.getName());

            binding.activeIndicator.setVisibility(
                    isSelected ? View.VISIBLE : View.INVISIBLE);

            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(category.getImageUrl())
                        .placeholder(R.drawable.ic_catalog)
                        .error(R.drawable.ic_catalog)
                        .into(binding.categoryIcon);
            } else {
                binding.categoryIcon.setImageResource(R.drawable.ic_catalog);
            }

            binding.categoryName.setTextColor(
                    binding.getRoot().getContext().getColor(
                            isSelected ? R.color.purple : R.color.textSecondary));
        }
    }

    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Category>() {
                @Override
                public boolean areItemsTheSame(@NonNull Category a, @NonNull Category b) {
                    return a.getId().equals(b.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Category a, @NonNull Category b) {
                    return a.equals(b);
                }
            };
}
