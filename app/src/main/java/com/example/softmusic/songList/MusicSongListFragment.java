package com.example.softmusic.songList;

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

import com.example.softmusic.interfaceListener.ChangeFragmentListener;
import com.example.softmusic.MainActivity;
import com.example.softmusic.R;

import java.util.ArrayList;
import java.util.List;

public class MusicSongListFragment extends Fragment {

    private final Context context;

    public MusicSongListFragment(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        com.example.softmusic.databinding.FragmentSongListBinding fragmentSongListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_song_list, container, false);

        List<MusicSongList> musicSongListList = new ArrayList<>();
        musicSongListList.add(new MusicSongList("我喜欢","4/17/22", 50,"me"));
        musicSongListList.add(new MusicSongList("我喜欢","4/17/22", 50,"me"));
        musicSongListList.add(new MusicSongList("我喜欢","4/17/22", 50,"me"));

        fragmentSongListBinding.songListList.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false));
        fragmentSongListBinding.songListList.setAdapter(new MusicSongListAdapter(context, musicSongListList, fragment -> ((MainActivity)requireActivity()).replaceFragment(fragment)));
        return fragmentSongListBinding.getRoot();
    }
}
