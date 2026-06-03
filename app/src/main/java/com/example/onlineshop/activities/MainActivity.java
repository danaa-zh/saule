package com.example.onlineshop.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ActivityMainBinding;
import com.example.onlineshop.ui.fragments.CatalogFragment;
import com.example.onlineshop.ui.fragments.FavoritesFragment;
import com.example.onlineshop.ui.fragments.HomeFragment;
import com.example.onlineshop.ui.fragments.ProfileFragment;

/**
 * Host activity for the 4 bottom-navigation tabs:
 *   Home | Catalog | Favorites | Profile
 *
 * Cart is a standalone Activity (CartActivity) — NOT a tab.
 * Uses hide/show fragment strategy to preserve state across tab switches.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private HomeFragment      homeFragment;
    private CatalogFragment   catalogFragment;
    private FavoritesFragment favoritesFragment;
    private ProfileFragment   profileFragment;

    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            initFragments();
        } else {
            // Restore fragment references after process death
            homeFragment      = (HomeFragment)      getSupportFragmentManager().findFragmentByTag("home");
            catalogFragment   = (CatalogFragment)   getSupportFragmentManager().findFragmentByTag("catalog");
            favoritesFragment = (FavoritesFragment) getSupportFragmentManager().findFragmentByTag("favorites");
            profileFragment   = (ProfileFragment)   getSupportFragmentManager().findFragmentByTag("profile");
            activeFragment    = homeFragment;
        }

        setupBottomNavigation();
    }

    private void initFragments() {
        homeFragment      = new HomeFragment();
        catalogFragment   = new CatalogFragment();
        favoritesFragment = new FavoritesFragment();
        profileFragment   = new ProfileFragment();

        activeFragment = homeFragment;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, profileFragment,  "profile").hide(profileFragment)
                .add(R.id.fragmentContainer, favoritesFragment,"favorites").hide(favoritesFragment)
                .add(R.id.fragmentContainer, catalogFragment,  "catalog").hide(catalogFragment)
                .add(R.id.fragmentContainer, homeFragment,     "home")
                .commit();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home)      return showFragment(homeFragment);
            if (id == R.id.nav_catalog)   return showFragment(catalogFragment);
            if (id == R.id.nav_favorites) return showFragment(favoritesFragment);
            if (id == R.id.nav_profile)   return showFragment(profileFragment);

            return false;
        });
    }

    private boolean showFragment(Fragment fragment) {
        if (fragment == null) return false;
        if (activeFragment == fragment) return true;

        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit();

        activeFragment = fragment;
        return true;
    }

    /** Called by HomeFragment's cart icon to open CartActivity. */
    public void openCart() {
        startActivity(new Intent(this, CartActivity.class));
    }

    /** Switches the bottom nav to the Catalog tab (called from HomeFragment category click). */
    public void navigateToCatalog(String categoryId, String categoryName) {
        if (catalogFragment != null) {
            catalogFragment.filterByCategory(categoryId, categoryName);
        }
        binding.bottomNavigation.setSelectedItemId(R.id.nav_catalog);
        showFragment(catalogFragment);
    }

    /** Updates the favorites badge count in the bottom nav. */
    public void updateFavoritesBadge(int count) {
        if (count > 0) {
            binding.bottomNavigation.getOrCreateBadge(R.id.nav_favorites).setNumber(count);
        } else {
            binding.bottomNavigation.removeBadge(R.id.nav_favorites);
        }
    }
}