package com.example.softmusic.playMusic

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.example.softmusic.R
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.room.DataBaseUtils
import com.tencent.mmkv.MMKV


class MediaPlaybackService : MediaBrowserServiceCompat() {
    private var mSession: MediaSessionCompat? = null
    private var mPlaybackState: PlaybackStateCompat? = null
    private lateinit var mMediaPlayer: MediaPlayer


    private var musicSongId:Long = 0
    private var musicSongListId:Long = 0

    private var playNum = 0
    var nowNum = 0
    private var list:List<MusicSong>? = null

    private val TAG = "MediaPlaybackService"

    override fun onCreate() {
        super.onCreate()
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

        mMediaPlayer = MediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        val kv = MMKV.defaultMMKV()
        kv.encode("musicSongId", list?.get(nowNum)?.musicSongId!!)
        kv.encode("musicSongListId",musicSongListId)
        mMediaPlayer.release()
        if (mSession != null) {
            mSession!!.release()
            mSession = null
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onUnsubscribe(id: String?) {
        super.onUnsubscribe(id)
        Log.d(TAG, "onUnsubscribe: ")
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int, rootHints: Bundle?
    ): BrowserRoot {
        Log.d(TAG, "onGetRoot")
        val kv = MMKV.defaultMMKV()
        musicSongId = rootHints?.getLong("musicSongId")?:kv.decodeLong("musicSongId")
        musicSongListId = rootHints?.getLong("musicSongListId")?:kv.decodeLong("musicSongListId")
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        Log.d(TAG, "onLoadChildren")
        result.detach()
        val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()
        val musicSong = DataBaseUtils.getMusicSongById(musicSongId)
        Log.d(TAG, "onLoadChildren: $musicSongId")
        list = DataBaseUtils.getPlayListsWithSongsById(musicSongListId)
        nowNum = list!!.indexOf(musicSong)
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
        }
        mMediaPlayer.setDataSource(musicSong.mediaFileUri)
        mMediaPlayer.prepareAsync()
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + R.raw.jay)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicSong.songTitle)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musicSong.duration.toLong())
            .build()
        mSession!!.isActive = true
        mSession!!.setMetadata(metadata)

        mPlaybackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
            .build()
        mSession!!.setPlaybackState(mPlaybackState)
        mMediaPlayer.start()

        result.sendResult(mediaItems)
    }

    private val mSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                Log.d(TAG, "onPlay")
                if (mPlaybackState!!.state == PlaybackStateCompat.STATE_PAUSED || mPlaybackState!!.state == PlaybackStateCompat.STATE_NONE
                    || mPlaybackState!!.state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT
                ) {
//                mExoPlayer?.play()
                    mMediaPlayer.start()
                    mPlaybackState = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING,
                            mMediaPlayer.currentPosition.toLong() / 1000, 1.0f)
                        .build()
                    mSession!!.setPlaybackState(mPlaybackState)
                }
            }

            override fun onPause() {
                super.onPause()
                Log.d(TAG, "onPause")
                if (mPlaybackState!!.state == PlaybackStateCompat.STATE_PLAYING) {
//                mExoPlayer?.pause()
                    mMediaPlayer.pause()
                    mPlaybackState = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PAUSED, mMediaPlayer.currentPosition.toLong() / 1000,
                            1.0f)
                        .build()
                    mSession!!.setPlaybackState(mPlaybackState)
                }
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                Log.d(TAG, "onSeekTo$pos")
                mMediaPlayer.seekTo(pos.toInt())
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                nowNum += 1
                Log.d(TAG, "onSkipToNext: $nowNum")
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

    private fun changeMusicSong(song: MusicSong){
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + R.raw.jay)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.songTitle )
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration.toLong())
            .build()
        mSession?.setMetadata(metadata)
        mMediaPlayer.stop()
        mMediaPlayer.reset()
        mMediaPlayer.setDataSource(song.mediaFileUri)
        mMediaPlayer.prepare()

        mPlaybackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, 0, 1.0f)
            .build()
        mSession!!.setPlaybackState(mPlaybackState)
        mMediaPlayer.start()
    }
}




