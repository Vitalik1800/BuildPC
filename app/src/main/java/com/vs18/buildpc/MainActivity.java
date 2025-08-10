package com.vs18.buildpc;

import android.annotation.*;
import android.content.*;
import android.os.*;
import android.util.*;

import androidx.annotation.Nullable;

import com.vs18.buildpc.databinding.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    int userId;
    Fragment userComponentsFragment = new UserComponentsFragment();
    MainFragment mainFragment = new MainFragment();
    MyConfigurationsFragment myConfigurationsFragment = new MyConfigurationsFragment();
    MyRulesFragment myRulesFragment = new MyRulesFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getUserId();
        Log.d("MainActivity", "UserId: " + userId);

        setCurrentFragment(mainFragment);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_main) {
                setCurrentFragment(mainFragment);
                return true;
            } else if (item.getItemId() == R.id.nav_components) {
                setCurrentFragment(userComponentsFragment);
                return true;
            } else if (item.getItemId() == R.id.nav_configs) {
                setCurrentFragment(myConfigurationsFragment);
                return true;
            } else if (item.getItemId() == R.id.nav_rules){
                setCurrentFragment(myRulesFragment);
                return true;
            } else if (item.getItemId() == R.id.nav_profile){
                setCurrentFragment(profileFragment);
                return true;
            }
            return false;
        });
    }

    private void setCurrentFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }

    private int getUserId(){
        SharedPreferences preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return preferences.getInt("userId", -1);
    }

}