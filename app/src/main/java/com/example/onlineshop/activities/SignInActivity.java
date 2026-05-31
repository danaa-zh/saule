package com.example.onlineshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ActivitySignInBinding;
import com.example.onlineshop.model.User;
import com.example.onlineshop.repository.AuthRepository;
import com.example.onlineshop.repository.FirebaseCallback;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.signInBtn.setOnClickListener(v -> attemptSignIn());

        binding.signUpLink.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });

        binding.forgotPasswordText.setOnClickListener(v -> {});
    }

    private void attemptSignIn() {
        String email    = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();

        if (!validateInputs(email, password)) return;

        setLoading(true);

        authRepository.signIn(email, password, new FirebaseCallback<User>() {
            @Override
            public void onSuccess(User result) {
                setLoading(false);
                navigateToMain();
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                binding.emailInputLayout.setError(errorMessage);
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        binding.emailInputLayout.setError(null);
        binding.passwordInputLayout.setError(null);

        if (TextUtils.isEmpty(email)) {
            binding.emailInputLayout.setError(getString(R.string.error_empty_email));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordInputLayout.setError(getString(R.string.error_empty_password));
            isValid = false;
        } else if (password.length() < 6) {
            binding.passwordInputLayout.setError(getString(R.string.error_short_password));
            isValid = false;
        }

        return isValid;
    }

    private void setLoading(boolean loading) {
        binding.signInBtn.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.signInBtn.setText(loading ? "" : getString(R.string.sign_in));
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
