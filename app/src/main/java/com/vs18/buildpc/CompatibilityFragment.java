package com.vs18.buildpc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vs18.buildpc.databinding.FragmentCompatibilityBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CompatibilityFragment extends Fragment {

    FragmentCompatibilityBinding binding;
    RequestQueue queue;
    String COMPONENT_URL = "http://10.0.2.2:3000/components";
    String COMPATIBILITY_URL = "http://10.0.2.2:3000/compatibility";
    List<String> componentList;
    List<Integer> componentIds;
    ArrayAdapter<String> componentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCompatibilityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queue = Volley.newRequestQueue(getContext());
        componentList = new ArrayList<>();
        componentIds = new ArrayList<>();

        componentAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, componentList);
        binding.spinnerComponentA.setAdapter(componentAdapter);
        binding.spinnerComponentB.setAdapter(componentAdapter);

        fetchComponentItems();

        int userId = getUserId();
        Log.d("CompatibilityActivity", "User ID: " + userId);

        binding.btnCompare.setOnClickListener(v -> saveCompare());
    }

    private int getUserId(){
        SharedPreferences preferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return preferences.getInt("userId", -1);
    }

    private void fetchComponentItems() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, COMPONENT_URL, null,
                response -> {
                    componentList.clear();
                    componentIds.clear();
                    for(int i = 0; i< response.length(); i++){
                        try{
                            JSONObject componentItem = response.getJSONObject(i);
                            int id = componentItem.getInt("id");
                            String name = componentItem.getString("name");
                            componentList.add(name);
                            componentIds.add(id);
                        } catch (JSONException e){
                            Log.e("Compatibility", "JSON Error: " + e.getMessage());
                        }
                    }
                    componentAdapter.notifyDataSetChanged();
                },
                error -> Log.e("CompatibilityActivity", "Volley Error: " + error.getMessage())
        );
        queue.add(request);
    }

    private void saveCompare(){
        JSONObject compatibilityObject = getCompatibilityDataFromFields();
        if(compatibilityObject == null) return;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, COMPATIBILITY_URL, compatibilityObject,
                response -> {
                    Toast.makeText(getContext(), "Порівняння компонентів збережено!", Toast.LENGTH_LONG).show();
                },
                error -> {
                    Log.e("CompatibilityActivity", "Volley Error: " + error.getMessage());
                    Toast.makeText(getContext(), "Помилка збереження!", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private JSONObject getCompatibilityDataFromFields() {
        int userId = getUserId();

        if (userId == -1) {
            Log.e("CompatibilityActivity", "Некоректні дані для порівняння компонентів!");
            return null;
        }

        int component_type_a = componentIds.get(binding.spinnerComponentA.getSelectedItemPosition());
        int component_type_b = componentIds.get(binding.spinnerComponentB.getSelectedItemPosition());

        try {
            JSONObject compatibilityObject = new JSONObject();
            compatibilityObject.put("user_id", userId);
            compatibilityObject.put("component_type_a", component_type_a);
            compatibilityObject.put("component_type_b", component_type_b);

            return compatibilityObject;
        }catch (JSONException e){
            Log.e("CompatibilityActivity", "Помилка формування JSON: " + e.getMessage());
            return null;
        }
    }
}
