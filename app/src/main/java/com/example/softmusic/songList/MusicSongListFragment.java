package com.example.softmusic.songList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.softmusic.MainActivity;
import com.example.softmusic.R;


public class MusicSongListFragment extends Fragment {

    public MusicSongListFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        com.example.softmusic.databinding.FragmentSongListBinding fragmentSongListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_song_list, container, false);

        MusicSongListViewModel model = new ViewModelProvider(requireActivity()).get(MusicSongListViewModel.class);

        fragmentSongListBinding.songListList.setLayoutManager(new GridLayoutManager(requireActivity(), 1, GridLayoutManager.VERTICAL, false));
        model.getMusicSongListData().observe(getViewLifecycleOwner(), musicSongLists ->{
                    fragmentSongListBinding.songListList.setAdapter(new MusicSongListAdapter(
                            requireContext(), musicSongLists));
                    ((MainActivity)requireActivity()).setTitle(MusicSongListViewModel.getTitle());
        });
        return fragmentSongListBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).showFAB();
    }
}
