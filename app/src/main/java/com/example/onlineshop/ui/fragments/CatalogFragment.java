package com.example.onlineshop.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onlineshop.activities.CartActivity;
import com.example.onlineshop.activities.ProductDetailActivity;
import com.example.onlineshop.databinding.FragmentCatalogBinding;
import com.example.onlineshop.model.Category;
import com.example.onlineshop.model.Product;
import com.example.onlineshop.repository.FirebaseCallback;
import com.example.onlineshop.repository.FavoritesRepository;
import com.example.onlineshop.repository.ProductRepository;
import com.example.onlineshop.ui.adapter.CategoryCatalogAdapter;
import com.example.onlineshop.ui.adapter.ProductAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CatalogFragment extends Fragment {

    private FragmentCatalogBinding  binding;
    private ProductRepository       productRepository;
    private FavoritesRepository     favoritesRepository;
    private CategoryCatalogAdapter  categoryAdapter;
    private ProductAdapter          productAdapter;

    private List<Category> allCategories    = new ArrayList<>();
    private String         activeCategoryId = null;
    private String         pendingCategoryId   = null;
    private String         pendingCategoryName = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productRepository   = new ProductRepository();
        favoritesRepository = new FavoritesRepository();

        setupAdapters();
        setupSearch();
        setupCartButton();
        loadCategories();

        if (pendingCategoryId != null) {
            applyPendingFilter();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavoriteIds();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void filterByCategory(String categoryId, String categoryName) {
        if (binding == null) {
            pendingCategoryId   = categoryId;
            pendingCategoryName = categoryName;
            return;
        }
        activeCategoryId = categoryId;
        updateTitle(categoryName);
        loadProductsByCategory(categoryId);

        int pos = categoryAdapter.findPositionById(categoryId);
        if (pos >= 0) {
            categoryAdapter.setSelectedPosition(pos);
            binding.categorySidebarRv.scrollToPosition(pos);
        }
    }

    private void setupAdapters() {
        categoryAdapter = new CategoryCatalogAdapter(category -> {
            activeCategoryId = category.getId();
            updateTitle(category.getName());
            clearSearch();
            loadProductsByCategory(category.getId());
        });
        binding.categorySidebarRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.categorySidebarRv.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Product product) {
                toggleFavorite(product);
            }
        });
        binding.productsRv.setAdapter(productAdapter);
    }

    private void setupSearch() {
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    if (activeCategoryId != null) {
                        loadProductsByCategory(activeCategoryId);
                    } else {
                        loadFeaturedProducts();
                    }
                } else {
                    searchProducts(query);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupCartButton() {
        binding.cartBtn.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CartActivity.class)));
    }

    private void loadCategories() {
        productRepository.getCategories(new FirebaseCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                if (!isAdded() || binding == null) return;
                allCategories = result;
                categoryAdapter.submitList(result);

                if (activeCategoryId != null) {
                    loadProductsByCategory(activeCategoryId);
                } else {
                    loadFeaturedProducts();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded() || binding == null) return;
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                loadFeaturedProducts();
            }
        });
    }

    private void loadFeaturedProducts() {
        setLoading(true);
        updateTitle("Catalog");

        productRepository.getFeaturedProducts(new FirebaseCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                if (!isAdded() || binding == null) return;
                setLoading(false);
                showProducts(result);
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded() || binding == null) return;
                setLoading(false);
                showEmptyState(true);
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductsByCategory(String categoryId) {
        setLoading(true);

        productRepository.getProductsByCategory(categoryId, new FirebaseCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                if (!isAdded() || binding == null) return;
                setLoading(false);
                showProducts(result);
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded() || binding == null) return;
                setLoading(false);
                showEmptyState(true);
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchProducts(String query) {
        setLoading(true);

        productRepository.searchProducts(query, new FirebaseCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                if (!isAdded() || binding == null) return;
                setLoading(false);
                showProducts(result);
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded() || binding == null) return;
                setLoading(false);
                showEmptyState(true);
            }
        });
    }

    private void loadFavoriteIds() {
        favoritesRepository.getFavorites(new FirebaseCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                if (!isAdded()) return;
                Set<String> ids = new HashSet<>();
                for (Product p : result) ids.add(p.getId());
                productAdapter.setFavoriteIds(ids);
            }

            @Override
            public void onFailure(String errorMessage) {}
        });
    }

    private void toggleFavorite(Product product) {
        favoritesRepository.toggleFavorite(product, new FirebaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isFavorite) {
                if (!isAdded()) return;
                String msg = isFavorite
                        ? product.getName() + " saved to favorites"
                        : product.getName() + " removed from favorites";
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProducts(List<Product> products) {
        if (products.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
            productAdapter.submitList(products);
            binding.productCountTv.setText(
                    getString(com.example.onlineshop.R.string.products_count, products.size()));
        }
    }

    private void updateTitle(String title) {
        if (binding != null) binding.titleTv.setText(title);
    }

    private void clearSearch() {
        if (binding != null) binding.searchInput.setText("");
    }

    private void setLoading(boolean loading) {
        if (binding == null) return;
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            binding.productsRv.setVisibility(View.GONE);
            binding.emptyState.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(boolean empty) {
        if (binding == null) return;
        binding.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.productsRv.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void applyPendingFilter() {
        String cid  = pendingCategoryId;
        String name = pendingCategoryName;
        pendingCategoryId   = null;
        pendingCategoryName = null;
        filterByCategory(cid, name);
    }
}