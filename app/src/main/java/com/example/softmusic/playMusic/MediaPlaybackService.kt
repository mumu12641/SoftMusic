package com.example.softmusic.playMusic

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.example.softmusic.R
import com.google.android.exoplayer2.ExoPlayer
import java.io.IOException

class MediaPlaybackService : MediaBrowserServiceCompat() {
    private var mSession: MediaSessionCompat? = null
    private var mPlaybackState: PlaybackStateCompat? = null
    private var mExoPlayer: ExoPlayer? = null
    private var mMediaPlayer: MediaPlayer? = null
    private val TAG = "MediaPlaybackService"
    override fun onCreate() {
        super.onCreate()
        //        mPlaybackState = new PlaybackStateCompat.Builder()
//                .setState(PlaybackStateCompat.STATE_NONE,0,1.0f)
//                .build();
        mPlaybackState = PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
        )
            .build()
        mSession = MediaSessionCompat(this, TAG)
        mSession!!.setPlaybackState(mPlaybackState)
        // 设置回调
        mSession!!.setCallback(mSessionCallback)
        mSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        sessionToken = mSession!!.sessionToken

//        mExoPlayer = new ExoPlayer.Builder(getApplicationContext()).build();
//        MediaItem mediaItem = MediaItem.fromUri(rawToUri(R.raw.jinglebells));
//        mExoPlayer.setMediaItem(mediaItem);
//        mExoPlayer.prepare();
        mMediaPlayer = MediaPlayer()
        try {
            mMediaPlayer!!.setDataSource(applicationContext, rawToUri(R.raw.jay))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mMediaPlayer!!.prepareAsync()
        mMediaPlayer!!.setOnPreparedListener { mediaPlayer: MediaPlayer? ->
            Log.d(
                TAG,
                "onPrepared: " + mMediaPlayer!!.duration
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        if (mExoPlayer != null) {
            mExoPlayer!!.release()
            mExoPlayer = null
        }
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
        if (mSession != null) {
            mSession!!.release()
            mSession = null
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int, rootHints: Bundle?
    ): BrowserRoot? {
        Log.d(TAG, "onGetRoot")
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        Log.d(TAG, "onLoadChildren")
        result.detach()
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + R.raw.jay)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "回到过去")
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mMediaPlayer!!.duration.toLong())
            .build()
        // 返回的数据是 MediaItem
        val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()
        mediaItems.add(
            MediaBrowserCompat.MediaItem(
                metadata.description,
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
            )
        )
        //向Browser发送数据
        mSession!!.isActive = true
        mSession!!.setMetadata(metadata)
        Log.d(TAG, "onLoadChildren: " + mMediaPlayer!!.duration)
        result.sendResult(mediaItems)
    }

    // 这个接口是响应控制器指令的回调
    private val mSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                Log.d(TAG, "onPlay")
                if (mPlaybackState!!.state == PlaybackStateCompat.STATE_PAUSED || mPlaybackState!!.state == PlaybackStateCompat.STATE_NONE) {
//                mExoPlayer.play();
                    mMediaPlayer!!.start()
                    mPlaybackState = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                        .build()
                    mSession!!.setPlaybackState(mPlaybackState)
                }
            }

            override fun onPause() {
                super.onPause()
                Log.d(TAG, "onPause")
                if (mPlaybackState!!.state == PlaybackStateCompat.STATE_PLAYING) {
//                mExoPlayer.pause();
                    mMediaPlayer!!.pause()
                    mPlaybackState = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PAUSED, 0, 1.0f)
                        .build()
                    mSession!!.setPlaybackState(mPlaybackState)
                }
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                Log.d(TAG, "onSeekTo$pos")
                //            mExoPlayer.seekTo(pos);
                mMediaPlayer!!.seekTo(pos.toInt())
            }
        }

    private fun rawToUri(id: Int): Uri {
        val uriStr = "android.resource://$packageName/$id"
        return Uri.parse(uriStr)
    }

    companion object {
        private const val MY_MEDIA_ROOT_ID = "media_root_id"
    }
}