package com.example.onlineshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import com.example.onlineshop.R;
import com.example.onlineshop.databinding.ActivitySplashBinding;
import com.example.onlineshop.repository.AuthRepository;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        authRepository = new AuthRepository();

        if (authRepository.isLoggedIn()) {
            navigateToMain();
            return;
        }

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();
        setupSignInSpannable();
    }
    private void setupClickListeners() {
        binding.startBtn.setOnClickListener(v -> navigateToSignUp());
    }

    private void setupSignInSpannable() {
        String fullText = getString(R.string.signin_plain);
        String clickablePart = "Sign In";

        SpannableString spannable = new SpannableString(fullText);
        int start = fullText.indexOf(clickablePart);
        int end = start + clickablePart.length();

        spannable.setSpan(new ForegroundColorSpan(
                        ContextCompat.getColor(this, R.color.purple)), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) { navigateToSignIn(); }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.textView3.setText(spannable);
        binding.textView3.setMovementMethod(LinkMovementMethod.getInstance());
        binding.textView3.setHighlightColor(
                ContextCompat.getColor(this, R.color.transparent));
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void navigateToSignIn() {
        startActivity(new Intent(this, SignInActivity.class));
    }

    private void navigateToSignUp() {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
