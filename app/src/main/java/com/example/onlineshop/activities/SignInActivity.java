package com.example.onlineshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlineshop.BuildConfig;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ActivitySignInBinding;
import com.example.onlineshop.model.User;
import com.example.onlineshop.repository.AuthRepository;
import com.example.onlineshop.repository.FirebaseCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private AuthRepository authRepository;
    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        Task<GoogleSignInAccount> task =
                                GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleGoogleSignInResult(task);
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = new AuthRepository();

        setupGoogleSignIn();
        setupClickListeners();
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupClickListeners() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.signInBtn.setOnClickListener(v -> attemptSignIn());

        binding.googleSignInBtn.setOnClickListener(v -> startGoogleSignIn());

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

    private void startGoogleSignIn() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            if (idToken == null) {
                showGoogleError("Google sign-in failed: missing token.");
                return;
            }

            setGoogleLoading(true);

            authRepository.firebaseAuthWithGoogle(idToken, new FirebaseCallback<User>() {
                @Override
                public void onSuccess(User result) {
                    setGoogleLoading(false);
                    navigateToMain();
                }

                @Override
                public void onFailure(String errorMessage) {
                    setGoogleLoading(false);
                    showGoogleError(errorMessage);
                }
            });

        } catch (ApiException e) {
            if (e.getStatusCode() != 12501) {
                showGoogleError("Google sign-in error: " + e.getStatusCode());
            }
        }
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

    private void setGoogleLoading(boolean loading) {
        binding.googleSignInBtn.setEnabled(!loading);
        binding.googleProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.googleSignInBtn.setText(loading ? "" : getString(R.string.sign_in_with_google));
    }

    private void showGoogleError(String message) {
        binding.emailInputLayout.setError(message);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
