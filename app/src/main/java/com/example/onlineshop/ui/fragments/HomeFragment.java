package com.example.onlineshop.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onlineshop.activities.CartActivity;
import com.example.onlineshop.activities.MainActivity;
import com.example.onlineshop.activities.ProductDetailActivity;
import com.example.onlineshop.databinding.FragmentHomeBinding;
import com.example.onlineshop.model.Category;
import com.example.onlineshop.model.Product;
import com.example.onlineshop.model.User;
import com.example.onlineshop.repository.AuthRepository;
import com.example.onlineshop.repository.FirebaseCallback;
import com.example.onlineshop.repository.ProductRepository;
import com.example.onlineshop.ui.adapter.CategoryHomeAdapter;
import com.example.onlineshop.ui.adapter.ProductAdapter;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private AuthRepository authRepository;
    private ProductRepository productRepository;
    private CategoryHomeAdapter categoryAdapter;
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository = new AuthRepository();
        productRepository = new ProductRepository();

        setupRecyclerViews();
        setupClickListeners();
        loadUserName();
        loadCategories();
        loadFeaturedProducts();
    }

    private void setupRecyclerViews() {
        categoryAdapter = new CategoryHomeAdapter(this::onCategoryClick);
        binding.categoriesRv.setLayoutManager(
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.HORIZONTAL, false));
        binding.categoriesRv.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                navigateToProductDetail(product);
            }

            @Override
            public void onFavoriteClick(Product product) {
                Toast.makeText(requireContext(),
                        product.getName() + " saved to favorites", Toast.LENGTH_SHORT).show();
            }
        });

        binding.productsRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.productsRv.setAdapter(productAdapter);
        binding.productsRv.setNestedScrollingEnabled(false);
    }

    private void setupClickListeners() {
        binding.cartBtn.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), CartActivity.class));
        });
    }

    private void loadUserName() {
        authRepository.fetchCurrentUser(new FirebaseCallback<User>() {
            @Override
            public void onSuccess(User result) {
                if (isAdded() && binding != null) {
                    binding.userNameTv.setText(result.getDisplayName());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }

    private void loadCategories() {
        productRepository.getCategories(new FirebaseCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                if (isAdded() && binding != null) {
                    categoryAdapter.submitList(result);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }

    private void loadFeaturedProducts() {
        setLoading(true);

        productRepository.getFeaturedProducts(new FirebaseCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                if (!isAdded() || binding == null) return;
                setLoading(false);
                if (result.isEmpty()) {
                    binding.emptyTv.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyTv.setVisibility(View.GONE);
                    productAdapter.submitList(result);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded() || binding == null) return;
                setLoading(false);
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onCategoryClick(Category category) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity())
                    .navigateToCatalog(category.getId(), category.getName());
        }
    }

    private void navigateToProductDetail(Product product) {
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }

    private void setLoading(boolean loading) {
        if (binding == null) return;
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
