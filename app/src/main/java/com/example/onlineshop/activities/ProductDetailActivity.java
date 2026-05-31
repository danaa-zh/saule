package com.example.onlineshop.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.onlineshop.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.onlineshop.databinding.ActivityProductDetailBinding;
import com.example.onlineshop.model.CartItem;
import com.example.onlineshop.model.Product;
import com.example.onlineshop.repository.CartRepository;
import com.example.onlineshop.repository.FirebaseCallback;
import com.example.onlineshop.repository.ProductRepository;

/**
 * Product detail screen.
 * Shows product image, name, price, rating, color picker, quantity selector.
 * Add to Cart calls CartRepository.
 * Follows SRP: display + interaction only.
 */
public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "product_id";

    private ActivityProductDetailBinding binding;
    private ProductRepository productRepository;
    private CartRepository    cartRepository;

    private Product currentProduct;
    private String  selectedColor = "";
    private int     quantity      = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productRepository = new ProductRepository();
        cartRepository    = new CartRepository();

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
    }

    private void loadProduct(String productId) {
        setLoading(true);

        productRepository.getProductById(productId, new FirebaseCallback<Product>() {
            @Override
            public void onSuccess(Product product) {
                setLoading(false);
                currentProduct = product;
                bindProduct(product);
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                Toast.makeText(ProductDetailActivity.this,
                        errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void bindProduct(Product product) {
        binding.productNameTv.setText(product.getName());
        binding.priceTv.setText(product.getFormattedPrice());
        binding.ratingTv.setText(
                String.format("%.1f  (%d reviews)",
                        product.getRating(), product.getReviewCount()));
        binding.quantityTv.setText(String.valueOf(quantity));

        // Load first image
        Glide.with(this)
                .load(product.getFirstImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_catalog)
                        .error(R.drawable.ic_catalog)
                        .transform(new RoundedCorners(32)))
                .into(binding.productImage);

        // Set first color as default selection
        if (product.getColors() != null && !product.getColors().isEmpty()) {
            selectedColor = product.getColors().get(0);
            binding.colorLabelTv.setText(getString(R.string.color_label, selectedColor));
        } else {
            binding.colorLabelTv.setVisibility(View.GONE);
            binding.colorsRow.setVisibility(View.GONE);
        }
    }

    private void addToCart() {
        if (currentProduct == null) return;

        binding.addToCartBtn.setEnabled(false);

        CartItem cartItem = new CartItem(currentProduct, selectedColor, quantity);

        cartRepository.addToCart(cartItem, new FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                binding.addToCartBtn.setEnabled(true);
                Toast.makeText(ProductDetailActivity.this,
                        "Added to cart!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                binding.addToCartBtn.setEnabled(true);
                Toast.makeText(ProductDetailActivity.this,
                        errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.contentGroup.setVisibility(loading ? View.GONE : View.VISIBLE);
    }
}