package com.example.softmusic.musicSong;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.softmusic.MainActivity;
import com.example.softmusic.databinding.FragmentSongBinding;
import com.example.softmusic.R;


public class MusicSongFragment extends Fragment implements View.OnClickListener {
    private FragmentSongBinding fragmentSongBinding;
    public MusicSongFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSongBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_song,container,false);
        MusicSongViewModel musicSongViewModel = new ViewModelProvider(requireActivity()).get(MusicSongViewModel.class);
        fragmentSongBinding.setTheMusicSongList(musicSongViewModel);
        fragmentSongBinding.songsList.setLayoutManager(new GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false));
        musicSongViewModel.getMusicSongData().observe(getViewLifecycleOwner(),
                musicSongs -> fragmentSongBinding.songsList.setAdapter(new MusicSongAdapter(requireContext(), musicSongs)));
        musicSongViewModel.getMusicSongListData().observe(getViewLifecycleOwner(),
                musicSongList -> {
                    ((MainActivity)requireActivity()).setTitle(musicSongList.getSongListTitle());
                    ((MainActivity)requireActivity()).setNavIcon(R.drawable.outline_arrow_back_24);
                    ((MainActivity)requireActivity()).getNavigationBar().setNavigationOnClickListener(this);
                    ((MainActivity)requireActivity()).hideFAB();


        });

        return fragmentSongBinding.getRoot();
    }

    @Override
    public void onClick(View view) {
        NavController controller = Navigation.findNavController(fragmentSongBinding.imageView);
        controller.navigate(R.id.action_musicSongFragment_to_musicSongListFragment);
        ((MainActivity)requireActivity()).getNavigationBar().setNavigationOnClickListener(null);
    }

}
