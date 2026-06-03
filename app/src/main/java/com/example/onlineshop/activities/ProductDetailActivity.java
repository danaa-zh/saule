package com.example.onlineshop.activities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ActivityProductDetailBinding;
import com.example.onlineshop.model.CartItem;
import com.example.onlineshop.model.Product;
import com.example.onlineshop.repository.CartRepository;
import com.example.onlineshop.repository.FavoritesRepository;
import com.example.onlineshop.repository.FirebaseCallback;
import com.example.onlineshop.repository.ProductRepository;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "product_id";

    private ActivityProductDetailBinding binding;
    private ProductRepository  productRepository;
    private CartRepository     cartRepository;
    private FavoritesRepository favoritesRepository;

    private Product currentProduct;
    private String selectedColor = "";
    private int quantity = 1;
    private boolean isFavorite = false;
    private View selectedSwatchView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productRepository = new ProductRepository();
        cartRepository = new CartRepository();
        favoritesRepository = new FavoritesRepository();

        String productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        if (productId == null) {
            finish();
            return;
        }

        setupClickListeners();
        loadProduct(productId);
    }

    private void setupClickListeners() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.decreaseBtn.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.quantityTv.setText(String.valueOf(quantity));
            }
        });

        binding.increaseBtn.setOnClickListener(v -> {
            quantity++;
            binding.quantityTv.setText(String.valueOf(quantity));
        });

        binding.addToCartBtn.setOnClickListener(v -> addToCart());

        binding.favoriteBtn.setOnClickListener(v -> toggleFavorite());
    }

    private void loadProduct(String productId) {
        setLoading(true);

        productRepository.getProductById(productId, new FirebaseCallback<Product>() {
            @Override
            public void onSuccess(Product product) {
                setLoading(false);
                currentProduct = product;
                bindProduct(product);
                checkFavoriteState(product.getId());
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                Toast.makeText(ProductDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void checkFavoriteState(String productId) {
        favoritesRepository.isFavorite(productId, new FirebaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isFavorite = result;
                updateFavoriteIcon();
            }

            @Override
            public void onFailure(String errorMessage) {}
        });
    }

    private void toggleFavorite() {
        if (currentProduct == null) return;

        favoritesRepository.toggleFavorite(currentProduct, new FirebaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isFavorite = result;
                updateFavoriteIcon();
                String msg = isFavorite
                        ? currentProduct.getName() + " saved to favorites"
                        : currentProduct.getName() + " removed from favorites";
                Toast.makeText(ProductDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ProductDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFavoriteIcon() {
        binding.favoriteBtn.setImageResource(
                isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        binding.favoriteBtn.setColorFilter(
                ContextCompat.getColor(this, isFavorite ? R.color.purple : R.color.textSecondary));
    }

    private void bindProduct(Product product) {
        binding.productNameTv.setText(product.getName());
        binding.priceTv.setText(product.getFormattedPrice());
        binding.ratingTv.setText(
                String.format("%.1f  (%d reviews)", product.getRating(), product.getReviewCount()));
        binding.quantityTv.setText(String.valueOf(quantity));

        Glide.with(this)
                .load(product.getFirstImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_catalog)
                        .error(R.drawable.ic_catalog)
                        .transform(new RoundedCorners(1)))
                .into(binding.productImage);

        List<String> colors = product.getColors();
        if (colors != null && !colors.isEmpty()) {
            selectedColor = colors.get(0);
            binding.colorLabelTv.setText(getString(R.string.color_label, selectedColor));
            buildColorSwatches(colors);
        } else {
            binding.colorLabelTv.setVisibility(View.GONE);
            binding.colorsRow.setVisibility(View.GONE);
        }
    }

    private void buildColorSwatches(List<String> colors) {
        binding.colorsRow.removeAllViews();

        int swatchSizePx = dpToPx(36);
        int swatchMarginPx = dpToPx(8);
        int borderWidthPx = dpToPx(2);
        int borderGapPx = dpToPx(3);

        for (int i = 0; i < colors.size(); i++) {
            final String colorName = colors.get(i);

            int fillColor;
            try {
                fillColor = Color.parseColor(colorName);
            } catch (IllegalArgumentException e) {
                fillColor = ContextCompat.getColor(this, R.color.lightGrey);
            }

            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(fillColor);

            View swatch = new View(this);
            android.widget.FrameLayout.LayoutParams swatchParams =
                    new android.widget.FrameLayout.LayoutParams(swatchSizePx, swatchSizePx);
            swatch.setLayoutParams(swatchParams);
            swatch.setBackground(circle);

            GradientDrawable ring = new GradientDrawable();
            ring.setShape(GradientDrawable.OVAL);
            ring.setColor(Color.TRANSPARENT);
            ring.setStroke(borderWidthPx, Color.TRANSPARENT);

            android.widget.FrameLayout frame = new android.widget.FrameLayout(this);
            android.widget.LinearLayout.LayoutParams frameParams =
                    new android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            frameParams.setMarginEnd(swatchMarginPx);
            frame.setLayoutParams(frameParams);
            frame.setPadding(borderGapPx, borderGapPx, borderGapPx, borderGapPx);
            frame.addView(swatch);
            frame.setBackground(ring);

            final android.widget.FrameLayout finalFrame = frame;
            final GradientDrawable finalRing = ring;
            frame.setOnClickListener(v -> {
                selectedColor = colorName;
                binding.colorLabelTv.setText(getString(R.string.color_label, colorName));
                updateSwatchSelection(finalFrame, finalRing, borderWidthPx);
            });

            binding.colorsRow.addView(frame);

            if (i == 0) {
                ring.setStroke(borderWidthPx, ContextCompat.getColor(this, R.color.purple));
                selectedSwatchView = frame;
            }
        }
    }

    private void updateSwatchSelection(android.widget.FrameLayout newFrame,
                                       GradientDrawable newRing,
                                       int borderWidthPx) {
        if (selectedSwatchView != null && selectedSwatchView.getBackground() instanceof GradientDrawable) {
            ((GradientDrawable) selectedSwatchView.getBackground())
                    .setStroke(borderWidthPx, Color.TRANSPARENT);
        }
        newRing.setStroke(borderWidthPx, ContextCompat.getColor(this, R.color.purple));
        selectedSwatchView = newFrame;
    }

    private void addToCart() {
        if (currentProduct == null) return;

        binding.addToCartBtn.setEnabled(false);

        CartItem cartItem = new CartItem(currentProduct, selectedColor, quantity);

        cartRepository.addToCart(cartItem, new FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                binding.addToCartBtn.setEnabled(true);
                Toast.makeText(ProductDetailActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                binding.addToCartBtn.setEnabled(true);
                Toast.makeText(ProductDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.contentGroup.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
