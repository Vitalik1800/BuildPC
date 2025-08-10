package com.vs18.buildpc;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.vs18.buildpc.databinding.FragmentAdminBinding;
import com.vs18.buildpc.databinding.FragmentMainBinding;

public class AdminFragment extends Fragment {

    private FragmentAdminBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnMain.setOnClickListener(v -> {
            replaceFragment(new AdminFragment());
        });
        binding.btnToComponents.setOnClickListener(v -> {
            replaceFragment(new ComponentsFragment());
        });
        binding.btnToUsers.setOnClickListener(v -> {
            replaceFragment(new UsersFragment());
        });
        binding.btnToConfigs.setOnClickListener(v -> {
            replaceFragment(new ConfigsFragment());
        });
        binding.btnToProfile.setOnClickListener(v -> {
            replaceFragment(new ProfileFragment());
        });
    }

    private void replaceFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .addToBackStack(null)
                .commit();
    }



}