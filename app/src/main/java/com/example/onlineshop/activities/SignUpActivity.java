package com.example.onlineshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ActivitySignUpBinding;
import com.example.onlineshop.repository.AuthRepository;
import com.example.onlineshop.repository.FirebaseCallback;
import com.example.onlineshop.model.User;

/**
 * Sign Up / Registration screen.
 * Validates inputs → delegates to AuthRepository → navigates to Main.
 */
public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.signUpBtn.setOnClickListener(v -> attemptSignUp());

        binding.signInLink.setOnClickListener(v -> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });
    }

    private void attemptSignUp() {
        String fullName         = binding.fullNameInput.getText().toString().trim();
        String email            = binding.emailInput.getText().toString().trim();
        String password         = binding.passwordInput.getText().toString().trim();
        String confirmPassword  = binding.confirmPasswordInput.getText().toString().trim();

        if (!validateInputs(fullName, email, password, confirmPassword)) return;

        setLoading(true);

        authRepository.signUp(fullName, email, password, new FirebaseCallback<User>() {
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

    private boolean validateInputs(String fullName, String email,
                                   String password, String confirmPassword) {
        boolean isValid = true;

        // Clear all errors first
        binding.fullNameInputLayout.setError(null);
        binding.emailInputLayout.setError(null);
        binding.passwordInputLayout.setError(null);
        binding.confirmPasswordInputLayout.setError(null);

        if (TextUtils.isEmpty(fullName)) {
            binding.fullNameInputLayout.setError(getString(R.string.error_empty_name));
            isValid = false;
        }

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

        if (!password.equals(confirmPassword)) {
            binding.confirmPasswordInputLayout.setError(
                    getString(R.string.error_passwords_not_match));
            isValid = false;
        }

        return isValid;
    }

    private void setLoading(boolean loading) {
        binding.signUpBtn.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.signUpBtn.setText(loading ? "" : getString(R.string.sign_up));
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}