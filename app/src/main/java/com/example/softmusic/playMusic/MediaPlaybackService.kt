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
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.room.DataBaseUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.tencent.mmkv.MMKV
import java.io.File
import java.lang.reflect.Array
import java.util.*
import kotlin.collections.ArrayList


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

    private var flag = true

    private lateinit var result:Result<MutableList<MediaBrowserCompat.MediaItem>>

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
        Log.d(TAG, "onGetRoot: $musicSongId")
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        Log.d(TAG, "onLoadChildren")

        result.detach()
        val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()
        if (flag) {
            loadMusic()
            flag = false
        }
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
        this.result = result
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
                Log.d(TAG, "onPlay")
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
                when {
                    mode == REPEAT_ONE -> {
                        mExoPlayer.seekTo(mExoPlayer.currentMediaItemIndex, 0L)
                    }
                    mExoPlayer.currentMediaItemIndex == list?.size?.minus(1) -> {
                        mExoPlayer.seekTo(0,0L)
                    }
                    else -> {
                        mExoPlayer.seekToNextMediaItem()
                    }
                }
                changeMusicSong(list?.get(mExoPlayer.currentMediaItemIndex)!!)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                when {
                    mode == REPEAT_ONE -> {
                        mExoPlayer.seekTo(mExoPlayer.currentMediaItemIndex, 0L)
                    }
                    mExoPlayer.currentMediaItemIndex == 0 -> {
                        mExoPlayer.seekTo(list?.size?.minus(1)!! ,0L)
                    }
                    else -> {
                        mExoPlayer.seekToPreviousMediaItem()
                    }
                }
                changeMusicSong(list?.get(mExoPlayer.currentMediaItemIndex)!!)
            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                super.onCustomAction(action, extras)
                when (action) {
                    CHANGE_MODE -> {
                        mode = extras?.getInt("order")!!
                        when (mode) {
                            DEFAULT -> {
                                mExoPlayer.repeatMode = Player.REPEAT_MODE_ALL

                                list = rawList
                                Log.d(TAG, "onCustomAction: $nowNum")
                                val item = mExoPlayer.getMediaItemAt(nowNum)
                                mExoPlayer.clearMediaItems()
                                reLoadMusic(list,mExoPlayer.currentPosition,item)
                            }
                            SHUFFLE -> {
                                val seed = 1
                                val position = mExoPlayer.currentPosition

                                nowNum = mExoPlayer.currentMediaItemIndex
                                val item = mExoPlayer.getMediaItemAt(nowNum)
                                list = rawList?.shuffled(kotlin.random.Random(seed))
                                mExoPlayer.clearMediaItems()

                                reLoadMusic(list,position,item)

                            }

                            REPEAT_ONE -> {
                                mExoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                                nowNum = mExoPlayer.currentMediaItemIndex
                                Log.d(TAG, "onCustomAction: $nowNum")
                            }
                        }
                    }
                    CHANGE_LIST -> {
                        musicSongId = extras?.getLong("musicSongId")!!
                        musicSongListId = extras.getLong("musicSongListId")
                        flag = true
                        Log.d(TAG, "change list: $musicSongId")
                        Log.d(TAG, "change list: $musicSongListId")
                        mExoPlayer.clearMediaItems()
                        this@MediaPlaybackService.notifyChildrenChanged(MY_MEDIA_ROOT_ID)
                    }
                    NEXT_TO_PLAY -> {
                        Log.d(TAG, "onCustomAction:  + next to play")
                        val id = extras?.getLong("id")!!
                        val url = extras.getString("url")!!
                        val title = extras.getString("title")!!
                        val duration = extras.getInt("duration")
                        val singer = extras.getString("singer")!!
                        val picture = extras.getString("picture")!!


                        val item =  MediaItem.fromUri(url)
                        mExoPlayer.addMediaItem(mExoPlayer.currentMediaItemIndex + 1,item)
                        val l =mutableListOf<MusicSong>()
                        list?.map {
                            l.add(it)
                        }
                        l.add(mExoPlayer.currentMediaItemIndex + 1,MusicSong(id,title,singer,picture,url,duration,1L))
                        list = l.toList()
                        Log.d(TAG, "onCustomAction: " + list!![mExoPlayer.currentMediaItemIndex]+
                                list!![mExoPlayer.currentMediaItemIndex  + 1] )
                        notifyChangeList()
                    }

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
        const val NEXT_TO_PLAY = "NEXT_TO_PLAY"

    }

    private fun notifyChangeList(){
        this.notifyChildrenChanged(MY_MEDIA_ROOT_ID)
    }

    private fun changeMusicSong(song: MusicSong) {
        val metadata = createMetadataFromMusic(song)
        mSession.setMetadata(metadata)
        updatePlayBackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT)
    }

    private fun loadMusic() {
        val musicSong = DataBaseUtils.getMusicSongById(musicSongId)
        var changeFlag = false
        list = DataBaseUtils.getPlayListsWithSongsById(musicSongListId)
        rawList = list
        nowNum = list!!.indexOf(musicSong)
        Log.d(TAG, "loadMusic: $nowNum")
        if (nowNum == -1){
            nowNum = 0
        }
        playNum = list!!.size

        for (i in list!!) {
            if (!File(i.mediaFileUri).exists()){
                Log.d(TAG, "loadMusic: not load")
                DataBaseUtils.deleteMusicSongRef(PlaylistSongCrossRef(musicSongListId,i.musicSongId))
                changeFlag = true
                continue
            }
            mExoPlayer.addMediaItem(MediaItem.fromUri(i.mediaFileUri))
        }
        mExoPlayer.seekTo(nowNum, 0)
        mExoPlayer.prepare()
        val metadata = createMetadataFromMusic(musicSong)
        mSession.isActive = true
        mSession.setMetadata(metadata)
        updatePlayBackState(PlaybackStateCompat.STATE_NONE)
        if (changeFlag) {
            val songList = DataBaseUtils.getMusicSongListById(musicSongListId)
            songList.songNumber = DataBaseUtils.getPlayListsWithSongsById(musicSongListId).size
            DataBaseUtils.updateMusicSongList(songList)
        }
    }

    private fun reLoadMusic(l:List<MusicSong>?,position:Long,item: MediaItem?){
        this.notifyChildrenChanged(MY_MEDIA_ROOT_ID)
        var j =0
        var index = 0
        if (l != null) {
            for (i in l) {
                mExoPlayer.addMediaItem(MediaItem.fromUri(i.mediaFileUri))
                if (MediaItem.fromUri(i.mediaFileUri) == item){
                    index = j
                }
                j++
            }
        }
        mExoPlayer.seekTo(index, 0L)
        nowNum = index
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
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,songAlbum)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM,albumId.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,mediaFileUri)
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




