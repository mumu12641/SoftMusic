package com.example.softmusic.playMusic

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.bumptech.glide.Glide
import com.example.softmusic.BaseApplication.Companion.context
import com.example.softmusic.MainActivity
import com.example.softmusic.R
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.room.DataBaseUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.tencent.mmkv.MMKV
import java.util.*


class MediaPlaybackService : MediaBrowserServiceCompat() {

    private val mSession: MediaSessionCompat by lazy {
        MediaSessionCompat(this, TAG)
    }
    private lateinit var mPlaybackState: PlaybackStateCompat
    private lateinit var mExoPlayer: ExoPlayer
    private var musicSongId: Long = 0
    private var musicSongListId: Long = 0
    private var mode = DEFAULT
    private var playNum = 0
    private var nowNum = 0
    private var list: List<MusicSong>? = null
    private var rawList:List<MusicSong>? = null
    private val TAG = "MediaPlaybackService"
    private lateinit var mReceiver: MediaActionReceiver
    private lateinit var manager: NotificationManager
    private lateinit var channelId: String

    override fun onCreate() {
        super.onCreate()
        mPlaybackState = PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackStateCompat.ACTION_STOP or
                    PlaybackStateCompat.ACTION_SEEK_TO or
                    PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        ).build()
        mSession.setPlaybackState(mPlaybackState)
        mSession.setCallback(mSessionCallback)
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mSession.isActive = true
        sessionToken = mSession.sessionToken
        mExoPlayer = ExoPlayer.Builder(this).build()

        mReceiver = MediaActionReceiver()
        val filter = IntentFilter().apply {
            addAction(ACTION_PREVIOUS)
            addAction(ACTION_PAUSE)
            addAction(ACTION_NEXT)
            addAction(ACTION_PLAY)
        }
        registerReceiver(mReceiver, filter)

        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        channelId =
            java.lang.String.valueOf(Random().nextInt())
        val mChannel: NotificationChannel?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel =
                NotificationChannel(channelId, "name", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            manager.createNotificationChannel(mChannel)
        }
    }


    @SuppressLint("RestrictedApi")
    override fun onUnsubscribe(id: String?) {
        super.onUnsubscribe(id)
        Log.d(TAG, "onUnsubscribe: ")
    }


    override fun onDestroy() {
        super.onDestroy()
        val kv = MMKV.defaultMMKV()
        Log.d(TAG, "onDestroy")
        kv.encode("musicSongId", list?.get(nowNum)?.musicSongId!!)
        kv.encode("musicSongListId", musicSongListId)
        mExoPlayer.release()
        mSession.release()
        unregisterReceiver(mReceiver)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int, rootHints: Bundle?
    ): BrowserRoot {
        val kv = MMKV.defaultMMKV()
        musicSongId = rootHints?.getLong("musicSongId") ?: kv.decodeLong("musicSongId")
        musicSongListId = rootHints?.getLong("musicSongListId") ?: kv.decodeLong("musicSongListId")
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.detach()
        val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()
        loadMusic()
        for (i in list!!) {
            val metadata = createMetadataFromMusic(i)
            mediaItems.add(
                MediaBrowserCompat.MediaItem(
                    metadata.description,
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
            )
        }
        result.sendResult(mediaItems)
    }

    inner class MediaActionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_PAUSE -> {
                    mSession.controller?.transportControls?.pause()
                }
                ACTION_NEXT -> {
                    mSession.controller?.transportControls?.skipToNext()
                }
                ACTION_PREVIOUS -> {
                    mSession.controller?.transportControls?.skipToPrevious()
                }
                ACTION_PLAY -> {
                    mSession.controller?.transportControls?.play()
                }
            }
        }
    }

    private val mSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPlay() {
                super.onPlay()
                if (mPlaybackState.state == PlaybackStateCompat.STATE_PAUSED || mPlaybackState.state == PlaybackStateCompat.STATE_NONE
                    || mPlaybackState.state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT || mPlaybackState.state == PlaybackStateCompat.STATE_PLAYING
                ) {
                    mExoPlayer.play()
                    updatePlayBackState(PlaybackStateCompat.STATE_PLAYING)
                    createNotification(
                        PlaybackStateCompat.STATE_PLAYING,
                        list?.get(mExoPlayer.currentMediaItemIndex)!!
                    )
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPause() {
                super.onPause()
                if (mPlaybackState.state == PlaybackStateCompat.STATE_PLAYING) {
                    mExoPlayer.pause()
                    updatePlayBackState(PlaybackStateCompat.STATE_PAUSED)
                    createNotification(
                        PlaybackStateCompat.STATE_PAUSED,
                        list?.get(mExoPlayer.currentMediaItemIndex)!!
                    )
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                mExoPlayer.seekTo(pos)
                mPlaybackState = PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, mExoPlayer.currentPosition, 1.0f)
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .build()
                mSession.setPlaybackState(mPlaybackState)
                createNotification(
                    PlaybackStateCompat.STATE_PLAYING,
                    list?.get(mExoPlayer.currentMediaItemIndex)!!
                )
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                Log.d(TAG, "onSkipToNext")
                if (mode == REPEAT_ONE) {
                    mExoPlayer.seekTo(mExoPlayer.currentMediaItemIndex, 0L)
                }else if (mExoPlayer.currentMediaItemIndex == list?.size?.minus(1)
                        && mode == DEFAULT) {
                    mExoPlayer.seekTo(0,0L)
                } else {
                    mExoPlayer.seekToNextMediaItem()
                }
                changeMusicSong(list?.get(mExoPlayer.currentMediaItemIndex)!!)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                if (mode == REPEAT_ONE) {
                    mExoPlayer.seekTo(mExoPlayer.currentMediaItemIndex, 0L)
                } else if (mExoPlayer.currentMediaItemIndex == 0 && mode == DEFAULT){
                    mExoPlayer.seekTo(list?.size?.minus(1)!! ,0L)
                } else {
                    mExoPlayer.seekToPreviousMediaItem()
                }
                changeMusicSong(list?.get(mExoPlayer.currentMediaItemIndex)!!)
            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                super.onCustomAction(action, extras)
                if (action == CHANGE_MODE) {
                    mode = extras?.getInt("order")!!
                    when (mode) {
                        DEFAULT -> {
                            mExoPlayer.repeatMode = Player.REPEAT_MODE_ALL
                            list = rawList
                            nowNum = extras.getInt("nowIndex")
                            mExoPlayer.clearMediaItems()
                            reLoadMusic(nowNum,list,mExoPlayer.currentPosition)
                        }
                        SHUFFLE -> {
                            val seed = extras.getInt("seed")
                            val nowIndex = extras.getInt("nowIndex")
                            val position = mExoPlayer.currentPosition
                            nowNum = nowIndex
                            list = list?.shuffled(kotlin.random.Random(seed))
                            mExoPlayer.clearMediaItems()
                            reLoadMusic(nowIndex,list,position)
                        }

                        REPEAT_ONE -> {
                            mExoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                        }
                    }
                } else if (action == CHANGE_LIST) {
                    musicSongId = extras?.getLong("musicSongId")!!
                    musicSongListId = extras.getLong("musicSongListId")
                    loadMusic()
                }
            }
        }


    companion object {
        private const val MY_MEDIA_ROOT_ID = "media_root_id"
        const val SHUFFLE = 1
        const val DEFAULT = 0
        const val REPEAT_ONE = 2
        const val ACTION_PAUSE = "PAUSE"
        const val ACTION_NEXT = "NEXT"
        const val ACTION_PREVIOUS = "PREVIOUS"
        const val ACTION_PLAY = "PLAY"

        const val CHANGE_MODE = "CHANGE_MODE"
        const val CHANGE_LIST = "CHANGE_LIST"

    }

    private fun changeMusicSong(song: MusicSong) {
        val metadata = createMetadataFromMusic(song)
        mSession.setMetadata(metadata)
        updatePlayBackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
    }

    private fun loadMusic() {
        val musicSong = DataBaseUtils.getMusicSongById(musicSongId)
        list = DataBaseUtils.getPlayListsWithSongsById(musicSongListId)
        rawList = list
        nowNum = list!!.indexOf(musicSong)
        playNum = list!!.size

        for (i in list!!) {
            mExoPlayer.addMediaItem(MediaItem.fromUri(i.mediaFileUri))
        }
        mExoPlayer.seekTo(nowNum, 0)
        mExoPlayer.prepare()
        val metadata = createMetadataFromMusic(musicSong)
        mSession.isActive = true
        mSession.setMetadata(metadata)
        updatePlayBackState(PlaybackStateCompat.STATE_NONE)
    }

    private fun reLoadMusic(index:Int,l:List<MusicSong>?,position:Long){
        if (l != null) {
            for (i in l) {
                mExoPlayer.addMediaItem(MediaItem.fromUri(i.mediaFileUri))
            }
        }
        mExoPlayer.seekTo(index, 0L)
        mExoPlayer.prepare()
        val metadata = l?.get(index)?.let { createMetadataFromMusic(it) }
        mSession.isActive = true
        mSession.setMetadata(metadata)
        updatePlayBackState(PlaybackStateCompat.STATE_NONE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createNotification(state: Int, song: MusicSong) {
        val controller = mSession.controller
        val mediaMetadata = controller?.metadata
        val description = mediaMetadata?.description!!
        val clickPendingIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val buidler = NotificationCompat.Builder(context, channelId).apply {
            setContentTitle(description.title)
            setContentText(description.subtitle)
            setSubText(description.description)
            setContentIntent(clickPendingIntent)
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(R.drawable.outline_music_note_black_24dp)
            addAction(
                NotificationCompat.Action(
                    R.drawable.outline_skip_previous_24,
                    "previous",
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(ACTION_PREVIOUS),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            )
            if (state == PlaybackStateCompat.STATE_PLAYING) {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.outline_pause_24,
                        "pause",
                        PendingIntent.getBroadcast(
                            context,
                            0, Intent(ACTION_PAUSE), PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                )
            } else if (state == PlaybackStateCompat.STATE_PAUSED) {
                addAction(
                    NotificationCompat.Action(
                        R.drawable.outline_play_arrow_24,
                        "play",
                        PendingIntent.getBroadcast(
                            context,
                            0, Intent(ACTION_PLAY), PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                )
            }
            addAction(
                NotificationCompat.Action(
                    R.drawable.outline_skip_next_24,
                    "next",
                    PendingIntent.getBroadcast(
                        context,
                        0, Intent(ACTION_NEXT), PendingIntent.FLAG_IMMUTABLE
                    )
                )
            )
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mSession.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
        }
        Thread {
            try {
                val bitmap: Bitmap = Glide
                    .with(context)
                    .asBitmap()
                    .load(song.songAlbum)
                    .submit()
                    .get()
                buidler.setLargeIcon(bitmap)
            } catch (e: Exception) {
                val bitmap: Bitmap = Glide
                    .with(context)
                    .asBitmap()
                    .load(R.drawable.card)
                    .submit()
                    .get()
                buidler.setLargeIcon(bitmap)
            }
            buidler.setProgress(0, 0, false)
            startForeground(1, buidler.build())
        }.start()
    }

    private fun createMetadataFromMusic(music: MusicSong): MediaMetadataCompat {
        return with(music) {
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, musicSongId.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songTitle)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songSinger)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toLong())
                .build()
        }
    }

    private fun updatePlayBackState(state: Int){
        mPlaybackState = PlaybackStateCompat.Builder()
            .setState(state, mExoPlayer.currentPosition, 1.0f)
            .setActions(
            PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_STOP or
                    PlaybackStateCompat.ACTION_SEEK_TO or
                    PlaybackStateCompat.ACTION_PLAY
            )
            .build()
        mSession.setPlaybackState(mPlaybackState)
    }
}




