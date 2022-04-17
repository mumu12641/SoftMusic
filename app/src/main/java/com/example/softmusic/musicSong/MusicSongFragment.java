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
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.softmusic.MainActivity;
import com.example.softmusic.databinding.FragmentSongBinding;
import com.example.softmusic.songList.MusicSongList;
import com.example.softmusic.R;

import java.util.ArrayList;
import java.util.List;

public class MusicSongFragment extends Fragment {
    private final Context context;
    private final MusicSongList musicSongList;

    public MusicSongFragment(Context context, MusicSongList musicSongList) {
        this.context = context;
        this.musicSongList = musicSongList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSongBinding fragmentSongBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_song,container,false);
        fragmentSongBinding.setTheMusicSongList(musicSongList);

        List<MusicSong> list = new ArrayList<>();
        list.add(new MusicSong("回到过去","周杰伦","八度空间",musicSongList));
        list.add(new MusicSong("回到过去","周杰伦","八度空间",musicSongList));
        list.add(new MusicSong("回到过去","周杰伦","八度空间",musicSongList));

        fragmentSongBinding.songsList.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false));
        fragmentSongBinding.songsList.setAdapter(new MusicSongAdapter(context, list, fragment -> ((MainActivity)requireActivity()).replaceFragment(fragment)));

        return fragmentSongBinding.getRoot();
    }
}
