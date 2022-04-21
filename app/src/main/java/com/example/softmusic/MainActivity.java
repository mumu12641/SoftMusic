package com.example.softmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.softmusic.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.color.DynamicColors;


public class MainActivity extends AppCompatActivity{
    private ActivityMainBinding activityMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding
                = DataBindingUtil.setContentView(this, R.layout.activity_main);
        DynamicColors.applyIfAvailable(this);
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment_activity_main);
        BottomNavigationView navigationView = activityMainBinding.navView;
        NavigationUI.setupWithNavController(navigationView,navController);
    }

    public void setTitle(String title){
        activityMainBinding.appBar.setTitle(title);
    }
}