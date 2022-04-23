package com.example.softmusic.playMusic;

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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediaPlaybackService extends MediaBrowserServiceCompat {

    private MediaSessionCompat mSession;
    private PlaybackStateCompat mPlaybackState;
    private ExoPlayer mExoPlayer;
    private MediaPlayer mMediaPlayer;

    private final String TAG = "MediaPlaybackService";
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";

    public MediaPlaybackService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mPlaybackState = new PlaybackStateCompat.Builder()
//                .setState(PlaybackStateCompat.STATE_NONE,0,1.0f)
//                .build();
        mPlaybackState = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PLAY_PAUSE)
                .build();
        mSession = new MediaSessionCompat(this,TAG);
        mSession.setPlaybackState(mPlaybackState);
        // 设置回调
        mSession.setCallback(mSessionCallback);
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mSession.getSessionToken());

//        mExoPlayer = new ExoPlayer.Builder(getApplicationContext()).build();
//        MediaItem mediaItem = MediaItem.fromUri(rawToUri(R.raw.jinglebells));
//        mExoPlayer.setMediaItem(mediaItem);
//        mExoPlayer.prepare();

        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(getApplicationContext(),rawToUri(R.raw.jay));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(mediaPlayer -> Log.d(TAG, "onPrepared: " + mMediaPlayer.getDuration()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer = null;
        }
        if (mMediaPlayer != null){
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
        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, ""+ R.raw.jay)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "回到过去")
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mMediaPlayer.getDuration())
                .build();
        // 返回的数据是 MediaItem
        ArrayList<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        mediaItems.add(new MediaBrowserCompat.MediaItem(
                metadata.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        ));
        //向Browser发送数据
        mSession.setActive(true);
        mSession.setMetadata(metadata);
        Log.d(TAG, "onLoadChildren: " + mMediaPlayer.getDuration());
        result.sendResult(mediaItems);
    }

    // 这个接口是响应控制器指令的回调
    private final MediaSessionCompat.Callback mSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            Log.d(TAG, "onPlay");
            if(mPlaybackState.getState() == PlaybackStateCompat.STATE_PAUSED || mPlaybackState.getState() == PlaybackStateCompat.STATE_NONE){
//                mExoPlayer.play();
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
//                mExoPlayer.pause();
                mMediaPlayer.pause();
                mPlaybackState = new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PAUSED,0,1.0f)
                        .build();
                mSession.setPlaybackState(mPlaybackState);
            }
        }
        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            Log.d(TAG, "onSeekTo" + pos);
//            mExoPlayer.seekTo(pos);
            mMediaPlayer.seekTo((int) pos);
        }
    };
    private Uri rawToUri(int id){
        String uriStr = "android.resource://" + getPackageName() + "/" + id;
        return Uri.parse(uriStr);
    }
}