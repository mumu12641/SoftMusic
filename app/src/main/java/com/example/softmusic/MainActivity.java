package com.example.softmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.softmusic.songList.MusicSongListFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.softmusic.databinding.ActivityMainBinding activityMainBinding
                = DataBindingUtil.setContentView(this, R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        MusicSongListFragment musicSongListFragment = new MusicSongListFragment(MainActivity.this);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.songListContent,musicSongListFragment);
        transaction.show(musicSongListFragment);
        transaction.commitAllowingStateLoss();

    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.songListContent,fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
}