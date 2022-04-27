package com.example.softmusic.playMusic

import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.example.softmusic.R
import com.example.softmusic.musicSong.MusicSong
import com.example.softmusic.room.DataBaseUtils
import com.google.android.exoplayer2.ExoPlayer

class MediaPlaybackService : MediaBrowserServiceCompat() {
    private var mSession: MediaSessionCompat? = null
    private var mPlaybackState: PlaybackStateCompat? = null
    private var mExoPlayer: ExoPlayer? = null
    private var mMediaPlayer: MediaPlayer? = null

    private var songTitle:String ?= null
    private var songListTitle:String ?= null

    private var playNum = 0
    var nowNum = 0
    private var list:List<MusicSong>? = null

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

        mExoPlayer = ExoPlayer.Builder(applicationContext).build()
//        MediaItem mediaItem = MediaItem.fromUri(rawToUri(R.raw.jinglebells));
//        mExoPlayer.setMediaItem(mediaItem);
//        mExoPlayer.prepare();
        mMediaPlayer = MediaPlayer()
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
    ): BrowserRoot {
        Log.d(TAG, "onGetRoot")
        // TODO get the bundle and set the playlist
        songTitle = rootHints?.getString("songTitle")
        songListTitle = rootHints?.getString("songListTitle")
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        Log.d(TAG, "onLoadChildren")
        result.detach()


        // TODO 上次app播放的地方
        var musicSong = MusicSong("jay.mp3","周杰伦","none","/storage/emulated/0/Download/jay.mp3"
        ,233509,3677281581483580442)

        val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()

        if (songTitle != null && songListTitle != null){
            Log.d(TAG, "onLoadChildren: $songTitle")
            musicSong = DataBaseUtils.getMusicSongByKey(songTitle.toString())
            list = DataBaseUtils.getPlayListsWithSongsByKey(songListTitle!!)
            playNum = list!!.size
            for (i in list!!){
                val metadata = MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + R.raw.jay)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, i.songTitle)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, i.duration.toLong())
                    .build()
                mediaItems.add(
                    MediaBrowserCompat.MediaItem(
                        metadata.description,
                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                    )
                )
//                mSession!!.isActive = true
//                mSession!!.setMetadata(metadata)
            }
//            result.sendResult(mediaItems)
        }
        mMediaPlayer?.setDataSource(musicSong.mediaFileUri)
        mMediaPlayer?.prepareAsync()
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + R.raw.jay)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicSong.songTitle)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musicSong.duration.toLong())
            .build()
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

            override fun onSkipToNext() {
                super.onSkipToNext()
                nowNum += 1
                Log.d(TAG, "onSkipToNext: $nowNum")
//                Log.d(TAG, "onSkipToNext: " + list?.size!!)

                if (nowNum == list?.size!!){
                    nowNum = 0
                }
                changeMusicSong(song = list?.get(nowNum)!!)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                nowNum -= 1
                if (nowNum < 0){
                    nowNum = list?.size?.minus(1)!!
                }
                changeMusicSong(song = list?.get(nowNum)!!)
            }
        }



    companion object {
        private const val MY_MEDIA_ROOT_ID = "media_root_id"
    }

    private fun changeMusicSong(song:MusicSong){
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + R.raw.jay)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.songTitle )
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration.toLong())
            .build()
        mSession?.setMetadata(metadata)

        mMediaPlayer?.stop()
        mMediaPlayer?.reset()

        mMediaPlayer?.setDataSource(song.mediaFileUri)
        mMediaPlayer?.prepare()
        mMediaPlayer?.setOnCompletionListener {
            Log.d(TAG, "changeMusicSong: done" + song.songTitle)
        }
        mPlaybackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
            .build()
        mSession!!.setPlaybackState(mPlaybackState)
    }
}