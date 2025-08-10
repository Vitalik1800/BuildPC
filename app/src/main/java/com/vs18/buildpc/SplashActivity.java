package com.vs18.buildpc;

import android.annotation.*;
import android.content.*;
import android.os.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.*;
import com.vs18.buildpc.databinding.ActivitySplashBinding;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    SharedPreferences preferences;
    String token, role;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        token = preferences.getString("token", null);
        role = preferences.getString("role", "user");

        new Handler().postDelayed(() -> {
            if(token != null){
                if("admin".equalsIgnoreCase(role)){
                    startActivity(new Intent(this, AdminActivity.class));
                } else{
                    startActivity(new Intent(this, MainActivity.class));
                }
            } else{
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }
}