package com.example.onlineshop.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlineshop.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> finish());

        binding.notificationsSwitch.setOnCheckedChangeListener((btn, isChecked) ->
                Toast.makeText(this,
                        isChecked ? "Notifications enabled" : "Notifications disabled",
                        Toast.LENGTH_SHORT).show());

        binding.languageRow.setOnClickListener(v ->
                Toast.makeText(this, "Language settings coming soon", Toast.LENGTH_SHORT).show());

        binding.privacyRow.setOnClickListener(v ->
                Toast.makeText(this, "Privacy policy coming soon", Toast.LENGTH_SHORT).show());
    }
}