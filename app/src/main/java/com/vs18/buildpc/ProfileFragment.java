package com.vs18.buildpc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vs18.buildpc.databinding.FragmentProfileBinding;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogout.setOnClickListener(v -> {
            logout();
        });
        loadUserProfile();
    }

    private void logout(){
        SharedPreferences preferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(getContext(), "Ви вийшли з облікового запису", Toast.LENGTH_LONG).show();

        startActivity(new Intent(getContext(), LoginActivity.class));
    }

    private void loadUserProfile() {
        String url = "http://10.0.2.2:3000/users/profile";

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try{
                        String name = response.getString("name");
                        String email = response.getString("email");

                        binding.textName.setText("Логін: " + name);
                        binding.textEmail.setText("Email: " + email);
                    }catch (JSONException e){
                        Log.e("PROFILE_ERROR", "JSON помилка: " + e.getMessage());
                        Toast.makeText(requireContext(), "Помилка обробки даних", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("PROFILE_ERROR", "Помилка завантаження профілю", error);
                    Toast.makeText(requireContext(), "Не вдалося завантажити профіль", Toast.LENGTH_SHORT).show();
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + getToken());
                return headers;
            }
        };

        queue.add(request);
    }

    private String getToken(){
        SharedPreferences preferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return preferences.getString("token", "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
