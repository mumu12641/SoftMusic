package com.example.softmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.example.softmusic.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.color.DynamicColors;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding
                = DataBindingUtil.setContentView(this, R.layout.activity_main);
        DynamicColors.applyIfAvailable(this);

    }
    public void setNavIcon(int resId){
        activityMainBinding.appBar.setNavigationIcon(resId);
    }
    public void setTitle(String title){
        activityMainBinding.appBar.setTitle(title);
        activityMainBinding.appBar.setNavigationIcon(null);
    }
    public MaterialToolbar getNavigationBar(){
        return activityMainBinding.appBar;
    }
    public void hideFAB(){
        activityMainBinding.floatingActionButton.hide();
    }
    public void showFAB(){
        activityMainBinding.floatingActionButton.show();
    }
}