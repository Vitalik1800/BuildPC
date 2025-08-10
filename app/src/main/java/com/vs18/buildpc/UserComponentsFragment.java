package com.vs18.buildpc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.vs18.buildpc.databinding.FragmentUserComponentsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserComponentsFragment extends Fragment {

    private FragmentUserComponentsBinding binding;
    private ComponentAdapter adapter;
    private List<Component> componentList;
    private RequestQueue queue;
    private static final String COMPONENT_URL = "http://10.0.2.2:3000/components";
    private int userId;
    CompatibilityFragment compatibilityFragment = new CompatibilityFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserComponentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = getUserId();
        Log.d("UserComponentsFragment", "UserId: " + userId);

        binding.recyclerViewComponents.setLayoutManager(new LinearLayoutManager(requireContext()));

        componentList = new ArrayList<>();
        adapter = new ComponentAdapter(requireActivity(), componentList);
        binding.recyclerViewComponents.setAdapter(adapter);

        queue = Volley.newRequestQueue(requireContext());
        fetchComponents();

        binding.btnCompare.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, compatibilityFragment) // переконайся, що `flFragment` — ID твого контейнера у layout'і
                    .addToBackStack(null)
                    .commit();
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Сортувати за:", "Назва ↑", "Назва ↓", "Ціна ↑", "Ціна ↓"}
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sortSpinner.setAdapter(spinnerAdapter);

        binding.sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1 -> sortByNameAsc();
                    case 2 -> sortByNameDesc();
                    case 3 -> sortByPriceAsc();
                    case 4 -> sortByPriceDesc();
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NotifyDataSetChanged")
    private void sortByNameAsc() {
        componentList.sort(Comparator.comparing(Component::getName));
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NotifyDataSetChanged")
    private void sortByNameDesc() {
        componentList.sort((c1, c2) -> c2.getName().compareTo(c1.getName()));
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NotifyDataSetChanged")
    private void sortByPriceAsc() {
        componentList.sort(Comparator.comparingDouble(Component::getPrice));
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NotifyDataSetChanged")
    private void sortByPriceDesc() {
        componentList.sort((c1, c2) -> Double.compare(c2.getPrice(), c1.getPrice()));
        adapter.notifyDataSetChanged();
    }

    private int getUserId() {
        SharedPreferences preferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return preferences.getInt("userId", -1);
    }

    private void fetchComponents() {
        @SuppressLint("NotifyDataSetChanged")
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                COMPONENT_URL,
                null,
                response -> {
                    try {
                        componentList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject componentItem = response.getJSONObject(i);
                            int id = componentItem.getInt("id");
                            String name = componentItem.getString("name");
                            String type = componentItem.getString("type");
                            String socket = componentItem.getString("socket");
                            String brand = componentItem.getString("brand");
                            double price = componentItem.getDouble("price");
                            String specs = componentItem.getString("specs");
                            componentList.add(new Component(id, name, type, socket, brand, price, specs));
                        }
                        adapter.setFullList(new ArrayList<>(componentList));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("UserComponentsFragment", "JSON Error: " + e.getMessage());
                    }
                }, error -> {
            Log.e("UserComponentsFragment", "Volley Error: " + error.getMessage());
            Toast.makeText(requireContext(), "Помилка отримання даних", Toast.LENGTH_LONG).show();
        });

        queue.add(jsonArrayRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}