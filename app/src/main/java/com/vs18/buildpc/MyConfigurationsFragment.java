package com.vs18.buildpc;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.vs18.buildpc.databinding.FragmentMyConfigurationsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyConfigurationsFragment extends Fragment {

    private FragmentMyConfigurationsBinding binding;
    private ConfigsAdapter orderAdapter;
    private final List<Config> orderList = new ArrayList<>();
    private int userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyConfigurationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = getUserId();

        Log.d("UserID", "User ID: " + userId);

        if (userId != -1) {
            loadOrders(userId);
        } else {
            Toast.makeText(requireContext(), "Помилка: ID користувача відсутній", Toast.LENGTH_LONG).show();
        }
    }

    private int getUserId() {
        SharedPreferences preferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return preferences.getInt("userId", -1);
    }

    private void loadOrders(int userId) {
        String url = "http://10.0.2.2:3000/configs/" + userId;
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("API_RESPONSE", "Response: " + response.toString());

                        orderList.clear();

                        if (response.length() == 0) {
                            Toast.makeText(requireContext(), "Немає конфігурацій", Toast.LENGTH_SHORT).show();
                        }

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject orderJson = response.getJSONObject(i);

                            int configId = orderJson.getInt("config_id");
                            String configName = orderJson.getString("config_name");
                            String createdAt = orderJson.getString("created_at");

                            JSONArray componentsJson = orderJson.getJSONArray("components");
                            List<Component> components = new ArrayList<>();
                            for (int j = 0; j < componentsJson.length(); j++) {
                                JSONObject componentJson = componentsJson.getJSONObject(j);
                                int componentId = componentJson.getInt("id");
                                String componentName = componentJson.getString("name");
                                components.add(new Component(componentId, componentName));
                            }

                            orderList.add(new Config(configId, configName, createdAt, components));
                        }

                        if (orderList.isEmpty()) {
                            Toast.makeText(requireContext(), "Немає конфігурацій для відображення", Toast.LENGTH_SHORT).show();
                        } else {
                            orderAdapter = new ConfigsAdapter(orderList);
                            binding.recyclerViewConfigs.setAdapter(orderAdapter);
                            binding.recyclerViewConfigs.setLayoutManager(new LinearLayoutManager(requireContext()));
                            orderAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        Log.e("API_ERROR", "Помилка: " + e.getMessage());
                    }
                },
                error -> Log.e("API_ERROR", "Помилка при завантаженні замовлень", error)
        );

        queue.add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
