package com.example.softmusic.musicSong;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.softmusic.MainActivity;
import com.example.softmusic.databinding.FragmentSongBinding;
import com.example.softmusic.songList.MusicSongList;
import com.example.softmusic.R;

import java.util.ArrayList;
import java.util.List;

public class MusicSongFragment extends Fragment {
    public MusicSongFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSongBinding fragmentSongBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_song,container,false);
        MusicSongViewModel musicSongViewModel = new ViewModelProvider(requireActivity()).get(MusicSongViewModel.class);
        fragmentSongBinding.setTheMusicSongList(musicSongViewModel);
        fragmentSongBinding.songsList.setLayoutManager(new GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false));
        musicSongViewModel.getMusicSongData().observe(getViewLifecycleOwner(), new Observer<List<MusicSong>>() {
            @Override
            public void onChanged(List<MusicSong> musicSongs) {
                fragmentSongBinding.songsList.setAdapter(new MusicSongAdapter(requireContext(), musicSongs));
            }
        });
        musicSongViewModel.getMusicSongListData().observe(getViewLifecycleOwner(), new Observer<MusicSongList>() {
            @Override
            public void onChanged(MusicSongList musicSongList) {
                ((MainActivity)requireActivity()).setTitleIcon(musicSongList.getSongListTitle());
            }
        });

        return fragmentSongBinding.getRoot();
    }
}
