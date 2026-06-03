package com.example.onlineshop.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.onlineshop.activities.MyOrdersActivity;
import com.example.onlineshop.activities.SettingsActivity;
import com.example.onlineshop.activities.SplashActivity;
import com.example.onlineshop.databinding.FragmentProfileBinding;
import com.example.onlineshop.model.User;
import com.example.onlineshop.repository.AuthRepository;
import com.example.onlineshop.repository.FirebaseCallback;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository = new AuthRepository();

        loadUserProfile();

        binding.myOrdersRow.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), MyOrdersActivity.class)));

        binding.settingsRow.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));

        binding.signOutBtn.setOnClickListener(v -> signOut());
    }

    private void loadUserProfile() {
        authRepository.fetchCurrentUser(new FirebaseCallback<User>() {
            @Override
            public void onSuccess(User result) {
                if (!isAdded() || binding == null) return;
                binding.userNameTv.setText(result.getDisplayName());
                binding.userEmailTv.setText(result.getEmail());
            }

            @Override
            public void onFailure(String errorMessage) {}
        });
    }

    private void signOut() {
        authRepository.signOut();
        Intent intent = new Intent(requireContext(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
