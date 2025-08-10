package com.vs18.buildpc;

import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.*;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.vs18.buildpc.databinding.*;
import org.json.*;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginButton.setOnClickListener(v -> loginUser());
        binding.registeredTextView.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void loginUser(){
        String userEmail = binding.email.getText().toString().trim();
        String userPassword = binding.password.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Будь ласка, введіть email та пароль!", Toast.LENGTH_LONG).show();
            return;
        }

        url = "http://10.0.2.2:3000/login";

        JSONObject postData = new JSONObject();
        try{
            postData.put("email", userEmail);
            postData.put("password_hash", userPassword);
        } catch (JSONException e) {
            Log.e("LoginActivity", "JSON Exception: " + e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    Log.d("LoginResponse", "Response: " + response.toString());
                    handleLoginSuccess(response);
                },
                this::handleLoginError
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void handleLoginSuccess(JSONObject response){
        String token = response.optString("token", null);

        JSONObject userObject = response.optJSONObject("user");
        if (userObject == null) {
            Toast.makeText(this, "Помилка обробки відповіді від сервера", Toast.LENGTH_LONG).show();
            return;
        }

        String role = userObject.optString("role", "user");
        int userId = userObject.optInt("id", -1);

        Log.d("LoginSuccess", "Token: " + token);
        Log.d("LoginSuccess", "Role: " + role);
        Log.d("LoginSuccess", "UserID: " + userId);

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putString("role", role);
        editor.putInt("userId", userId);
        editor.apply();

        Toast.makeText(this, "Авторизація успішна!", Toast.LENGTH_LONG).show();

        Intent intent;
        if("admin".equals(role)){
            intent = new Intent(this, AdminActivity.class);
        } else{
            intent = new Intent(this, MainActivity.class);
        }
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }


    private void handleLoginError(VolleyError error) {
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            String responseString = new String(error.networkResponse.data);

            Log.e("LoginError", "Статус код: " + statusCode);
            Log.e("LoginError", "Відповідь: " + responseString);

            try {
                JSONObject errorResponse = new JSONObject(responseString);
                String errorMessage = errorResponse.optString("message", "Помилка авторизації");

                if (statusCode == 403) {
                    Toast.makeText(this, "Акаунт не активний. Зверніться до адміністратора.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(this, "Невідома помилка. Спробуйте ще раз.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Помилка з'єднання. Перевірте мережу.", Toast.LENGTH_LONG).show();
        }
    }
}