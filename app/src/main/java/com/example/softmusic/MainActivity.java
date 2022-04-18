package com.example.softmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.softmusic.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding
                = DataBindingUtil.setContentView(this, R.layout.activity_main);

    }
    public void setTitleIcon(String title){
        activityMainBinding.appBar.setTitle(title);
    }
}