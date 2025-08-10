package com.vs18.buildpc;

import static android.content.Intent.getIntent;

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
import com.vs18.buildpc.databinding.FragmentAddComponentBinding;
import com.vs18.buildpc.databinding.FragmentAdminBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentAddComponent extends Fragment {

    FragmentAddComponentBinding binding;
    RequestQueue queue;
    private static final String BASE_URL = "http://10.0.2.2:3000/components";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddComponentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queue = Volley.newRequestQueue(getContext());

        Bundle args = getArguments();
        final int[] finalComponentId = { -1 };

        if (args != null) {
            finalComponentId[0] = args.getInt("id", -1);

            binding.btnAddComponent.setText("Оновити компонент");

            String name = args.getString("name");
            String type = args.getString("type");
            String socket = args.getString("socket");
            String brand = args.getString("brand");
            double price = args.getDouble("price", 0.0);
            String specs = args.getString("specs");

            if (name != null) binding.etName.setText(name);
            if (type != null) binding.etType.setText(type);
            if (socket != null) binding.etSocket.setText(socket);
            if (brand != null) binding.etBrand.setText(brand);
            binding.etPrice.setText(String.valueOf(price));
            if (specs != null) binding.etSpecs.setText(specs);
        } else {
            Log.d("AddComponentActivity", "Фрагмент запущено без аргументів — режим додавання");
            binding.btnAddComponent.setText("Додати компонент");
        }

        binding.btnAddComponent.setOnClickListener(v -> {
            if (finalComponentId[0] == -1) {
                addComponent();
            } else{
                updateComponent(finalComponentId[0]);
            }
        });
    }

    private void addComponent(){
        JSONObject componentData = getComponentData();
        if(componentData == null) return;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, componentData,
                response -> {
                    Toast.makeText(getContext(), "Компонент додано!", Toast.LENGTH_LONG).show();
                }, error -> {
            Log.e("AddComponentActivity", "Помилка додавання: " + error.getMessage());
            Toast.makeText(getContext(), "Помилка під час додавання!", Toast.LENGTH_LONG).show();
        });

        queue.add(request);
    }

    private void updateComponent(int id){
        JSONObject componentData = getComponentData();
        if(componentData == null) return;

        String updateURL = BASE_URL + "/" + id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, updateURL, componentData,
                response -> {
                    Toast.makeText(getContext(), "Компонент оновлено!", Toast.LENGTH_LONG).show();
                },
                error -> {
                    Log.e("AddComponentActivity", "Помилка оновлення: " + error.getMessage());
                    Toast.makeText(getContext(), "Помилка під час оновлення!", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private JSONObject getComponentData(){
        String Name = binding.etName.getText().toString();
        String Type = binding.etType.getText().toString();
        String Socket = binding.etSocket.getText().toString();
        String Brand = binding.etBrand.getText().toString();
        String Price = binding.etPrice.getText().toString();
        String Specs = binding.etSpecs.getText().toString();

        if(Name.isEmpty() || Type.isEmpty() || Socket.isEmpty() || Brand.isEmpty() || Price.isEmpty() || Specs.isEmpty()){
            Toast.makeText(getContext(), "Всі поля обов'язкові!", Toast.LENGTH_LONG).show();
            return null;
        }

        JSONObject componentData = new JSONObject();
        try{
            componentData.put("name", Name);
            componentData.put("type", Type);
            componentData.put("socket", Socket);
            componentData.put("brand", Brand);
            componentData.put("price", Double.parseDouble(Price));
            componentData.put("specs", Specs);
        }catch (JSONException e){
            Log.e("AddComponentActivity", "JSON Помилка: " + e.getMessage());
            return null;
        }
        return componentData;
    }
}
