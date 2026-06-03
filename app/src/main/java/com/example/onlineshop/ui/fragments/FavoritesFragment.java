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

import com.example.onlineshop.activities.MainActivity;
import com.example.onlineshop.activities.ProductDetailActivity;
import com.example.onlineshop.databinding.FragmentFavoritesBinding;
import com.example.onlineshop.model.Product;
import com.example.onlineshop.repository.FavoritesRepository;
import com.example.onlineshop.repository.FirebaseCallback;
import com.example.onlineshop.ui.adapter.ProductAdapter;

import java.util.List;

/**
 * Favorites screen fragment.
 * Displays all products the user has saved using FavoritesRepository.
 * Follows SRP: view logic only. Data access via FavoritesRepository.
 */
public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private FavoritesRepository      favoritesRepository;
    private ProductAdapter           productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favoritesRepository = new FavoritesRepository();

        setupRecyclerView();
        loadFavorites();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh list whenever user returns to this tab
        // (e.g. after removing a favorite from ProductDetail)
        loadFavorites();
    }

    // ─── Setup ─────────────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Product product) {
                // On favorites screen — tapping the heart removes from favorites
                removeFromFavorites(product);
            }
        });

        binding.favoritesRv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.favoritesRv.setAdapter(productAdapter);
    }

    // ─── Data ──────────────────────────────────────────────────────────────────

    private void loadFavorites() {
        setLoading(true);

        favoritesRepository.getFavorites(new FirebaseCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                if (!isAdded() || binding == null) return;
                setLoading(false);

                if (result.isEmpty()) {
                    showEmptyState(true);
                } else {
                    showEmptyState(false);
                    productAdapter.submitList(result);
                }

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateFavoritesBadge(result.size());
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

    private void removeFromFavorites(Product product) {
        favoritesRepository.removeFromFavorites(product.getId(), new FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (!isAdded() || binding == null) return;
                Toast.makeText(requireContext(),
                        product.getName() + " removed from favorites",
                        Toast.LENGTH_SHORT).show();
                loadFavorites(); // refresh list
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded() || binding == null) return;
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── UI State ──────────────────────────────────────────────────────────────

    private void setLoading(boolean loading) {
        if (binding == null) return;
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            binding.favoritesRv.setVisibility(View.GONE);
            binding.emptyState.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(boolean empty) {
        if (binding == null) return;
        binding.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.favoritesRv.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}