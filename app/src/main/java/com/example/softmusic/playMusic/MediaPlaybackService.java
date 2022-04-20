package com.example.softmusic.playMusic;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.example.softmusic.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediaPlaybackService extends MediaBrowserServiceCompat {

//    创建并初始化媒体会话
//    设置媒体会话回调
//    设置媒体会话令牌

    private MediaSessionCompat mSession;
    private PlaybackStateCompat mPlaybackState;
    private MediaPlayer mMediaPlayer;

    private final String TAG = "MediaPlaybackService";
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";

    public MediaPlaybackService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPlaybackState = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE,0,1.0f)
                .build();
        mSession = new MediaSessionCompat(this,TAG);
        mSession.setPlaybackState(mPlaybackState);

        // 设置回调
        mSession.setCallback(mSessionCallback);

        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //设置token后会触发MediaBrowserCompat.ConnectionCallback的回调方法
        //表示MediaBrowser与MediaBrowserService连接成功
        setSessionToken(mSession.getSessionToken());

//        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(this, R.raw.jinglebells);
//        mediaPlayer.start(); // no need to call prepare(); create() does that for you
//        mMediaPlayer.setOnCompletionListener(mCompletionListener);
//        mMediaPlayer.setOnPreparedListener(mPreparedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mSession != null) {
            mSession.release();
            mSession = null;
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                                 int clientUid, @Nullable Bundle rootHints) {
        Log.d(TAG, "onGetRoot");
        return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,
                               @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "onLoadChildren");
        result.detach();
        //我们模拟获取数据的过程，真实情况应该是异步从网络或本地读取数据
        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, ""+ R.raw.jinglebells)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "圣诞歌")
                .build();
        // 返回的数据是 MediaItem
        ArrayList<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        mediaItems.add(createMediaItem(metadata));

        //向Browser发送数据
        result.sendResult(mediaItems);
    }

    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata){
        return new MediaBrowserCompat.MediaItem(
                metadata.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        );
    }

    // 这个接口是响应控制器指令的回调
    private final MediaSessionCompat.Callback mSessionCallback = new MediaSessionCompat.Callback() {
        // 例如 这个是在 MediaController.getTransportControls().play 时候回调
        @Override
        public void onPlay() {
            super.onPlay();
            Log.d(TAG, "onPlay");
            if(mPlaybackState.getState() == PlaybackStateCompat.STATE_PAUSED || mPlaybackState.getState() == PlaybackStateCompat.STATE_NONE){
//                mMediaPlayer.set
                mMediaPlayer.start();
                mPlaybackState = new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING,0,1.0f)
                        .build();
                mSession.setPlaybackState(mPlaybackState);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.d(TAG, "onPause");
            if(mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING){
                mMediaPlayer.pause();
                mPlaybackState = new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PAUSED,0,1.0f)
                        .build();
                mSession.setPlaybackState(mPlaybackState);
            }
        }

        @SuppressLint("SwitchIntDef")
        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            super.onPlayFromUri(uri, extras);
            try {
                switch (mPlaybackState.getState()){
                    case PlaybackStateCompat.STATE_PLAYING:
                    case PlaybackStateCompat.STATE_PAUSED:
                    case PlaybackStateCompat.STATE_NONE:
                        mMediaPlayer.reset();
                        mMediaPlayer.setDataSource(MediaPlaybackService.this,uri);
                        mMediaPlayer.prepare();//准备同步
                        mPlaybackState = new PlaybackStateCompat.Builder()
                                .setState(PlaybackStateCompat.STATE_CONNECTING,0,1.0f)
                                .build();
                        mSession.setPlaybackState(mPlaybackState);
                        //我们可以保存当前播放音乐的信息，以便客户端刷新UI
                        mSession.setMetadata(new MediaMetadataCompat.Builder()
                                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,extras.getString("title"))
                                .build()
                        );
                        break;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onPlayFromSearch(String query, Bundle extras) {
            super.onPlayFromSearch(query, extras);
            Log.d(TAG, "onPlayFromSearch");

        }
    };
//     // 监听MediaPlayer.prepare()
//    private final MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
//        @Override
//        public void onPrepared(MediaPlayer mediaPlayer) {
//            mMediaPlayer.start();
//            mPlaybackState = new PlaybackStateCompat.Builder()
//                    .setState(PlaybackStateCompat.STATE_PLAYING,0,1.0f)
//                    .build();
//            mSession.setPlaybackState(mPlaybackState);
//        }
//    } ;
//
//
//     //监听播放结束的事件
//    private final MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
//        @Override
//        public void onCompletion(MediaPlayer mediaPlayer) {
//            mPlaybackState = new PlaybackStateCompat.Builder()
//                    .setState(PlaybackStateCompat.STATE_NONE,0,1.0f)
//                    .build();
//            mSession.setPlaybackState(mPlaybackState);
//            mMediaPlayer.reset();
//        }
//    };
}