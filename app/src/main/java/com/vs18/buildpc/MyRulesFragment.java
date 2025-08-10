package com.vs18.buildpc;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vs18.buildpc.databinding.FragmentMyRulesBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyRulesFragment extends Fragment {

    private FragmentMyRulesBinding binding;
    private CompatibilityAdapter compatibilityAdapter;
    private final List<CompatibilityRule> compatibilityRuleList = new ArrayList<>();
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyRulesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = getUserId();

        Log.d("UserID", "User ID: " + userId);

        if(userId != -1){
            loadRules(userId);
        } else{
            Toast.makeText(requireContext(), "Помилка: ID користувача відсутній", Toast.LENGTH_LONG).show();
        }
    }

    private void loadRules(int userId){
        String url = "http://10.0.2.2:3000/compatibility/" + userId;
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("API_RESPONSE", "Response: " + response.toString());

                        compatibilityRuleList.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject rule = response.getJSONObject(i);
                            int ruleId = rule.getInt("id");
                            boolean is_compatible = rule.getInt("is_compatible") == 1;

                            JSONObject componentA = rule.getJSONObject("componentA");
                            JSONObject componentB = rule.getJSONObject("componentB");

                            List<Component> components = new ArrayList<>();
                            components.add(new Component(componentA.getInt("id"), componentA.getString("name")));
                            components.add(new Component(componentB.getInt("id"), componentB.getString("name")));

                            compatibilityRuleList.add(new CompatibilityRule(ruleId, is_compatible, components));
                        }

                        compatibilityAdapter = new CompatibilityAdapter(compatibilityRuleList);
                        binding.recyclerViewRules.setAdapter(compatibilityAdapter);
                        binding.recyclerViewRules.setLayoutManager(new LinearLayoutManager(requireContext()));
                        compatibilityAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("API_ERROR", "Помилка обробки JSON: " + e.getMessage());
                    }
                },
                error -> Log.e("API_ERROR", "Помилка при завантаженні правил", error)
        );

        queue.add(request);
    }


    private int getUserId() {
        SharedPreferences preferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return preferences.getInt("userId", -1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
