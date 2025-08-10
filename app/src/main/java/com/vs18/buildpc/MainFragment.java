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

import com.vs18.buildpc.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnMain.setOnClickListener(v -> {
            replaceFragment(new MainFragment());
        });
        binding.btnComponents.setOnClickListener(v -> {
            replaceFragment(new UserComponentsFragment());
        });
        binding.btnConfigs.setOnClickListener(v -> {
            replaceFragment(new MyConfigurationsFragment());
        });
        binding.btnRules.setOnClickListener(v -> {
            replaceFragment(new MyRulesFragment());
        });
        binding.btnProfile.setOnClickListener(v -> {
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