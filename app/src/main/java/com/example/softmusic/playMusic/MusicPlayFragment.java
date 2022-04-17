package com.example.softmusic.playMusic;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.softmusic.R;
import com.example.softmusic.databinding.FragmentMusicPlayBinding;



public class MusicPlayFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private FragmentMusicPlayBinding binding;
    private final Context context;


    public MusicPlayFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_music_play,container,false);
        binding.seekBar.setOnSeekBarChangeListener(this);
        binding.playsong.setOnClickListener(this);
        binding.nextsong.setOnClickListener(this);
        binding.lastsong.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onClick(View view) {
    }

}
