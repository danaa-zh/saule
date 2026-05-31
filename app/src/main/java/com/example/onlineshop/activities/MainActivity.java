package com.example.onlineshop.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ActivityMainBinding;
import com.example.onlineshop.ui.fragments.CartFragment;
import com.example.onlineshop.ui.fragments.CatalogFragment;
import com.example.onlineshop.ui.fragments.FavoritesFragment;
import com.example.onlineshop.ui.fragments.HomeFragment;
import com.example.onlineshop.ui.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private HomeFragment      homeFragment;
    private CatalogFragment   catalogFragment;
    private CartFragment      cartFragment;
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
        }

        setupBottomNavigation();
    }

    private void initFragments() {
        homeFragment      = new HomeFragment();
        catalogFragment   = new CatalogFragment();
        cartFragment      = new CartFragment();
        favoritesFragment = new FavoritesFragment();
        profileFragment   = new ProfileFragment();

        activeFragment = homeFragment;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainer, profileFragment,  "profile").hide(profileFragment)
                .add(R.id.fragmentContainer, favoritesFragment,"favorites").hide(favoritesFragment)
                .add(R.id.fragmentContainer, cartFragment,     "cart").hide(cartFragment)
                .add(R.id.fragmentContainer, catalogFragment,  "catalog").hide(catalogFragment)
                .add(R.id.fragmentContainer, homeFragment,     "home")
                .commit();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home)      return showFragment(homeFragment);
            if (id == R.id.nav_catalog)   return showFragment(catalogFragment);
            if (id == R.id.nav_cart)      return showFragment(cartFragment);
            if (id == R.id.nav_favorites) return showFragment(favoritesFragment);
            if (id == R.id.nav_profile)   return showFragment(profileFragment);

            return false;
        });
    }

    private boolean showFragment(Fragment fragment) {
        if (activeFragment == fragment) return true;

        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit();

        activeFragment = fragment;
        return true;
    }

    public void updateCartBadge(int count) {
        if (count > 0) {
            binding.bottomNavigation.getOrCreateBadge(R.id.nav_cart).setNumber(count);
        } else {
            binding.bottomNavigation.removeBadge(R.id.nav_cart);
        }
    }
}
