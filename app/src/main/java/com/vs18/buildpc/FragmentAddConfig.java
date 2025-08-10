package com.vs18.buildpc;

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
import com.vs18.buildpc.databinding.FragmentAddConfigBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentAddConfig extends Fragment {

    private FragmentAddConfigBinding binding;
    private RequestQueue queue;
    private final String CONFIG_URL = "http://10.0.2.2:3000/configs";

    private int userId;
    private ArrayList<ComponentItem> componentItemList;
    private String componentName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddConfigBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queue = Volley.newRequestQueue(requireContext());

        // Отримуємо дані з аргументів
        Bundle args = getArguments();
        if (args != null) {
            userId = args.getInt("userId", -1);
            componentName = args.getString("name", "");
            componentItemList = (ArrayList<ComponentItem>) args.getSerializable("componentList");

            Log.d("FragmentAddConfig", "userId: " + userId);
            binding.componentNameTextView.setText("Компонент: " + componentName);
        } else {
            Log.e("FragmentAddConfig", "Аргументи не передані!");
        }

        // Обробка кнопки
        binding.btnSaveConfig.setOnClickListener(v -> saveConfig());
    }

    private void saveConfig() {
        JSONObject configObject = getConfigDataFromFields();
        if (configObject == null) return;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, CONFIG_URL, configObject,
                response -> {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Log.e("FragmentAddConfig", "Помилка розбору відповіді: " + e.getMessage());
                    }
                    requireActivity().getSupportFragmentManager().popBackStack(); // Назад
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorMessage = new String(error.networkResponse.data);
                        Log.e("FragmentAddConfig", "Помилка: " + errorMessage);
                        Toast.makeText(getContext(), "Помилка: " + errorMessage, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("FragmentAddConfig", "Помилка: " + error.getMessage());
                        Toast.makeText(getContext(), "Не вдалося відправити конфігурацію", Toast.LENGTH_LONG).show();
                    }
                });

        queue.add(request);
    }

    private JSONObject getConfigDataFromFields() {
        String configName = binding.etConfigName.getText().toString().trim();

        if (userId == -1 || componentItemList == null || componentItemList.isEmpty() || configName.isEmpty()) {
            Log.e("FragmentAddConfig", "Некоректні дані для конфігурації!");
            Toast.makeText(getContext(), "Всі поля обов'язкові", Toast.LENGTH_LONG).show();
            return null;
        }

        JSONArray componentsArray = new JSONArray();

        try {
            for (ComponentItem componentItem : componentItemList) {
                JSONObject componentObject = new JSONObject();
                componentObject.put("component_id", componentItem.getComponentId());
                componentsArray.put(componentObject);
            }

            JSONObject configObject = new JSONObject();
            configObject.put("user_id", userId);
            configObject.put("config_name", configName);
            configObject.put("components", componentsArray);

            return configObject;

        } catch (JSONException e) {
            Log.e("FragmentAddConfig", "Помилка формування JSON: " + e.getMessage());
            return null;
        }
    }
}
