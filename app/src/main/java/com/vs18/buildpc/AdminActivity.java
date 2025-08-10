package com.vs18.buildpc;

import android.annotation.*;
import android.content.*;
import android.os.*;
import android.util.*;

import androidx.annotation.Nullable;

import com.vs18.buildpc.databinding.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AdminActivity extends AppCompatActivity {

    ActivityAdminBinding binding;
    int userId;
    AdminFragment adminFragment = new AdminFragment();
    ComponentsFragment componentsFragment = new ComponentsFragment();
    UsersFragment usersFragment = new UsersFragment();
    ConfigsFragment configsFragment = new ConfigsFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getUserId();
        Log.d("AdminActivity", "UserId: " + userId);

        setCurrentFragment(adminFragment);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_to_admin) {
                setCurrentFragment(adminFragment);
                return true;
            } else if(item.getItemId() == R.id.nav_components){
                setCurrentFragment(componentsFragment);
                return true;
            } else if(item.getItemId() == R.id.nav_users){
                setCurrentFragment(usersFragment);
                return true;
            } else if(item.getItemId() == R.id.nav_configs){
                setCurrentFragment(configsFragment);
                return true;
            } else if(item.getItemId() == R.id.nav_profile){
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