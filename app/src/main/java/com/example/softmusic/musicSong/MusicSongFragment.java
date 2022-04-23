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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.softmusic.MainActivity;
import com.example.softmusic.databinding.FragmentSongBinding;
import com.example.softmusic.R;
import com.example.softmusic.room.PlaylistSongCrossRef;
import com.example.softmusic.songList.MusicSongListFragment;
import com.example.softmusic.songList.MusicSongListViewModel;


public class MusicSongFragment extends Fragment {
    private FragmentSongBinding fragmentSongBinding;
    public MusicSongFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSongBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_song,container,false);
        assert getArguments() != null;
        MusicSongViewModel musicSongViewModel =
                new ViewModelProvider(requireActivity(),
                        new ViewModelFactory(requireContext(),getArguments().getString("key"))).get(MusicSongViewModel.class);
        fragmentSongBinding.setTheMusicSongList(musicSongViewModel);
        fragmentSongBinding.songsList.setLayoutManager(new GridLayoutManager(
                requireContext(), 1, GridLayoutManager.VERTICAL, false));
//        musicSongViewModel.insertMusicSongRef(new PlaylistSongCrossRef("我喜欢","aaa"));
//        musicSongViewModel.insertMusicSong(new MusicSong("aaa","周杰伦","八度空间","none"));
        musicSongViewModel.getTest().observe(getViewLifecycleOwner(),
                musicSongs -> fragmentSongBinding.songsList.setAdapter(new MusicSongAdapter(requireContext(), musicSongs)));
        musicSongViewModel.getTestList().observe(getViewLifecycleOwner(),
                musicSongList -> ((MainActivity)requireActivity()).setTitle(musicSongList.getSongListTitle()));

        return fragmentSongBinding.getRoot();
    }

    static class ViewModelFactory implements ViewModelProvider.Factory{
        private final Context context;
        private final String key;

        public ViewModelFactory(Context context,String key) {
            this.context = context;
            this.key = key;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
            return (T)new MusicSongViewModel(context,key);
        }
    }

}
