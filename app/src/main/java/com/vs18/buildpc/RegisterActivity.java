package com.vs18.buildpc;

import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.*;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.vs18.buildpc.databinding.*;
import org.json.*;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    String URL = "http://10.0.2.2:3000/register";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser(){
        String Name = binding.name.getText().toString();
        String Email = binding.email.getText().toString();
        String Password = binding.password.getText().toString();


        if(Name.isEmpty() || Email.isEmpty() || Password.isEmpty()){
            Toast.makeText(this, "Будь ласка заповніть всі поля!", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject postData = new JSONObject();
        try{
            postData.put("name", Name);
            postData.put("email", Email);
            postData.put("password_hash", Password);
        } catch (JSONException e){
            Log.e("RegisterActivity", "Error: " + e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, postData,
                response -> {
                    Log.d("RegisterActivity", "Відповідь сервера: " + response.toString());
                    Toast.makeText(this, "Реєстрація успішна!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, LoginActivity.class));
                },
                error -> {
                    Log.e("RegisterError", "Помилка: " + error.getMessage());
                    Toast.makeText(this, "Реєстрація неуспішна!", Toast.LENGTH_LONG).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}