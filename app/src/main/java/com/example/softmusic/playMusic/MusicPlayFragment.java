package com.example.softmusic.playMusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.softmusic.MainActivity;
import com.example.softmusic.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;


public class MusicPlayFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    public MusicPlayFragment(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        com.example.softmusic.databinding.FragmentMusicPlayBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_music_play, container, false);
//        binding.seekBar.setOnSeekBarChangeListener(this);
//        binding.playsong.setOnClickListener(this);
//        binding.nextsong.setOnClickListener(this);
//        binding.lastsong.setOnClickListener(this);
//        ((MainActivity)requireActivity()).getNavigationBar().setNavigationOnClickListener(this);

        ExoPlayer player = new ExoPlayer.Builder(requireContext()).build();
        binding.videoView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3");
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();



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
//        switch (view.getId()){
//            case R.id.playsong:
//                break;
//            case R.id.lastsong:
//                break;
//            case R.id.nextsong:
//                break;
//            default:
//                NavController controller = Navigation.findNavController(binding.imageView2);
//                controller.navigate(R.id.action_musicPlayFragment_to_musicSongFragment);
//                ((MainActivity)requireActivity()).getNavigationBar().setNavigationOnClickListener(null);
//                break;

        }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).setTitle("播放");
        ((MainActivity)requireActivity()).hideFAB();
    }
}
