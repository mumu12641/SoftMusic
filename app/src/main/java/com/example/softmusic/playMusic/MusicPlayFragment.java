package com.example.softmusic.playMusic;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.softmusic.MainActivity;
import com.example.softmusic.R;
import com.example.softmusic.databinding.FragmentMusicPlayBinding;

import java.util.List;
import java.util.Objects;


public class MusicPlayFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private MediaBrowserCompat mBrowser;
    private MediaControllerCompat mController;
    private final String TAG = "MediaPlayer";
    private FragmentMusicPlayBinding binding;

    private int nowPos = 0;
    private int lastPos = -1;


    public MusicPlayFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBrowser = new MediaBrowserCompat(requireContext(),
                new ComponentName(requireContext(),MediaPlaybackService.class), //绑定服务
                mBrowserConnectionCallback, // 设置回调
                null);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBrowser.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBrowser.disconnect();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_music_play, container, false);
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
        int pos = seekBar.getProgress();
        nowPos = pos / 1000;
        lastPos = -1;
        mController.getTransportControls().seekTo(pos);
        mController.getTransportControls().play();
        binding.playsong.setBackgroundResource(R.drawable.outline_pause_24);
    }

    @SuppressLint({"SwitchIntDef", "NonConstantResourceId"})
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.playsong) {
            if (mController != null) {
                switch (mController.getPlaybackState().getState()) {
                    case PlaybackStateCompat.STATE_PLAYING:
                        mController.getTransportControls().pause();
                        lastPos = nowPos;
                        break;
                    case PlaybackStateCompat.STATE_PAUSED:
                        mController.getTransportControls().play();
                        lastPos = -1;
                        break;
                    case PlaybackStateCompat.STATE_NONE:
                        mController.getTransportControls().play();
                        updateProgressThread mUpdateProgressThread = new updateProgressThread();
                        mUpdateProgressThread.start();
                        break;
                }

                Log.d(TAG, "onClick: " + nowPos);
                Log.d(TAG, "onClick: " + lastPos);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).setTitle("Play");
    }

    // 连接状态的回调接口，连接成功的时候会调用这里的onConnected方法
    private final MediaBrowserCompat.ConnectionCallback mBrowserConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback(){
        @Override
        public void onConnected() {
            super.onConnected();
            Log.d(TAG, "onConnected");
            if (mBrowser.isConnected()){
                // 如果连接成功 这里的mediaId就为MediaBrowserService.onGetRoot的返回值
                String mediaId = mBrowser.getRoot();
                mBrowser.unsubscribe(mediaId);
                mBrowser.subscribe(mediaId,
                        mBrowserSubscriptionCallback // 这里是订阅回调接口，当Service读取数据后会把数据发送回来，调用这个回调接口
                );
                mController = new MediaControllerCompat(requireContext(),mBrowser.getSessionToken());
                // 给Controller注册回调
                mController.registerCallback(mMediaControllerCallback);
            }

            MediaMetadataCompat metadataCompat = mController.getMetadata();
            updateDuration(metadataCompat);
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
        }
    };

    // 向媒体浏览器服务(MediaBrowserService)发起数据订阅请求的回调接口
    private final MediaBrowserCompat.SubscriptionCallback mBrowserSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                                     @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            Log.d(TAG, "onChildrenLoaded");
            // children 就是Service返回回来的数据
            for (MediaBrowserCompat.MediaItem item:children){
                Log.d(TAG,
                        Objects.requireNonNull(item.getDescription().getTitle()).toString());
            }

            MediaMetadataCompat metadataCompat = mController.getMetadata();
            updateDuration(metadataCompat);
        }
    };

    // 这个是Controller 的回调，通过这个回调来更新UI
    private final MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @SuppressLint("SwitchIntDef")
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            switch (state.getState()){
                case PlaybackStateCompat.STATE_NONE:
                    Toast.makeText(requireContext(),"none",Toast.LENGTH_LONG).show();
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    binding.playsong.setBackgroundResource(R.drawable.outline_play_arrow_24);
                    Toast.makeText(requireContext(),"pause",Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onPlaybackStateChanged: " + (int) state.getPosition());
                    break;
                case PlaybackStateCompat.STATE_PLAYING:
                    binding.playsong.setBackgroundResource(R.drawable.outline_pause_24);
                    Toast.makeText(requireContext(),"play",Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onPlaybackStateChanged: " + (int) state.getPosition());
                    break;
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
        }
    };

    private void updateDuration(MediaMetadataCompat metadataCompat){
        if (metadataCompat != null){
            int duration = (int) metadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            Log.d(TAG, "updateDuration: " + duration);
            if (duration > 0){
                binding.seekBar.setMax(duration);
            }
        } else {
            Log.d(TAG, "updateDuration: null" );
        }
    }

    private class updateProgressThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (binding.seekBar.getProgress() < binding.seekBar.getMax()){
                if (nowPos == lastPos){
                    continue;
                }
                nowPos ++;
                binding.seekBar.setProgress(nowPos*1000);
                SystemClock.sleep(1000);
            }
        }
    }
}
