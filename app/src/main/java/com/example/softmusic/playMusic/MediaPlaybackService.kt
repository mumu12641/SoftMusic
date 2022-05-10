package com.example.softmusic.playMusic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.example.softmusic.BaseApplication.Companion.context
import com.example.softmusic.R
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.room.DataBaseUtils
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.tencent.mmkv.MMKV
import java.util.*


class MediaPlaybackService : MediaBrowserServiceCompat() {

    private var mSession: MediaSessionCompat? = null
    private var mPlaybackState: PlaybackStateCompat? = null
    private lateinit var mExoPlayer:ExoPlayer
    private var musicSongId:Long = 0
    private var musicSongListId:Long = 0
    private var mode = DEFAULT
    private var playNum = 0
    private var nowNum = 0
    private var list:List<MusicSong>? = null
    private val TAG = "MediaPlaybackService"

    private lateinit var mReceiver:MediaActionReceiver



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
        )
            .build()
        mSession = MediaSessionCompat(this, TAG)
            mSession!!.setPlaybackState(mPlaybackState)
        // 设置回调
        mSession!!.setCallback(mSessionCallback)
        mSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mSession!!.isActive = true
        sessionToken = mSession!!.sessionToken

        mExoPlayer = ExoPlayer.Builder(this).build()


        mReceiver = MediaActionReceiver()
        val filter = IntentFilter().apply {
            addAction(ACTION_PREVIOUS)
            addAction(ACTION_PAUSE)
            addAction(ACTION_NEXT)
        }
        registerReceiver(mReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        val kv = MMKV.defaultMMKV()
        kv.encode("musicSongId", list?.get(nowNum)?.musicSongId!!)
        kv.encode("musicSongListId",musicSongListId)
        mExoPlayer.release()
        if (mSession != null) {
            mSession!!.release()
            mSession = null
        }
        unregisterReceiver(mReceiver)
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
        loadMusic()
        for (i in list!!){
            val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + i.musicSongId.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, i.songTitle)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, i.duration.toLong())
//                    .putBitmap(MediaMetadataCompat.METADATA)
                .build()
            mediaItems.add(
                MediaBrowserCompat.MediaItem(
                    metadata.description,
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
            )
        }

        result.sendResult(mediaItems)
    }

    inner class MediaActionReceiver :BroadcastReceiver() {
        private val TAG = "MediaActionReceiver"
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
//        onReceive: android.intent.action.MEDIA_BUTTON
            Log.d(TAG, "onReceive: $action")
            when (action){
                ACTION_PAUSE -> {
                    mSession?.controller?.transportControls?.pause()
                }
                ACTION_NEXT ->{
                    mSession?.controller?.transportControls?.skipToNext()
                }
                ACTION_PREVIOUS -> {
                    mSession?.controller?.transportControls?.skipToPrevious()
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
                if (mPlaybackState!!.state == PlaybackStateCompat.STATE_PAUSED || mPlaybackState!!.state == PlaybackStateCompat.STATE_NONE
                    || mPlaybackState!!.state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT
                ) {
                    mExoPlayer.play()
                    mPlaybackState = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING,
                            mExoPlayer.currentPosition / 1000, 1.0f,SystemClock.elapsedRealtime())
                        .build()
                    mSession!!.setPlaybackState(mPlaybackState)
                    createNotification()
                }
            }

            override fun onPause() {
                super.onPause()
                Log.d(TAG, "onPause")
                if (mPlaybackState!!.state == PlaybackStateCompat.STATE_PLAYING) {
                    mExoPlayer.pause()
                    mPlaybackState = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PAUSED, mExoPlayer.currentPosition / 1000,
                            1.0f,SystemClock.elapsedRealtime())
                        .build()
                    mSession!!.setPlaybackState(mPlaybackState)
                }
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                Log.d(TAG, "onSeekTo$pos")
                mExoPlayer.seekTo(pos)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                Log.d(TAG, "onSkipToNext")
                mExoPlayer.seekToNextMediaItem()
                changeMusicSong(list?.get(mExoPlayer.currentMediaItemIndex)!!)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                Log.d(TAG, "onSkipToPrevious")
                mExoPlayer.seekToPreviousMediaItem()
                changeMusicSong(list?.get(mExoPlayer.currentMediaItemIndex)!!)
            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                super.onCustomAction(action, extras)
                if (action == "0"){
                    // 切换播放顺序
                    mode = extras?.getInt("order")!!
                    when(mode){
                        DEFAULT -> {
                            mExoPlayer.repeatMode = Player.REPEAT_MODE_ALL
                        }
                        SHUFFLE -> {
//                            mExoPlayer.repeatMode = Player.SHUFF
//                            mExoPlayer.shuffleModeEnabled = true
                        }
                        REPEAT_ONE -> {
                            mExoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                        }
                    }
                } else if (action == "1"){
                    // TODO 更换曲目
                    musicSongId = extras?.getLong("musicSongId")!!
                    musicSongListId = extras.getLong("musicSongListId")
                    Log.d(TAG, "onCustomAction: 1")
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

    }

    private fun changeMusicSong(song: MusicSong){
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + song.musicSongId.toString())
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.songTitle )
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration.toLong())
            .build()
        mSession?.setMetadata(metadata)
        mPlaybackState = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, 0, 1.0f)
            .build()
        mSession!!.setPlaybackState(mPlaybackState)
    }

    private fun loadMusic(){
        val musicSong = DataBaseUtils.getMusicSongById(musicSongId)
        list = DataBaseUtils.getPlayListsWithSongsById(musicSongListId)
        nowNum = list!!.indexOf(musicSong)
        playNum = list!!.size

        for (i in list!!){
            mExoPlayer.addMediaItem(MediaItem.fromUri(i.mediaFileUri))
            Log.d(TAG, "loadMusic: " + i.mediaFileUri)
        }
        mExoPlayer.seekTo(nowNum,0)
        mExoPlayer.prepare()
        val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, musicSong.musicSongId.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicSong.songTitle)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musicSong.duration.toLong())
//                .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,BitmapFactory.decodeResource(resources,R.drawable.card))
                .build()
        mSession!!.isActive = true
        mSession!!.setMetadata(metadata)
        mPlaybackState = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                .build()
        mSession!!.setPlaybackState(mPlaybackState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createNotification(){
        val controller = mSession?.controller
        val mediaMetadata = controller?.metadata
        val description = mediaMetadata?.description!!
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelId: String =
            java.lang.String.valueOf(Random().nextInt())
        val mChannel: NotificationChannel?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = NotificationChannel(channelId, "name", NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            manager.createNotificationChannel(mChannel)

        }
        val buidler = NotificationCompat.Builder(context, channelId).apply {
            setContentTitle(description.title)
            setContentText(description.subtitle)
            setSubText(description.description)
            setLargeIcon(description.iconBitmap)

            setContentIntent(controller.sessionActivity)

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
//                            )
                    PendingIntent.getBroadcast(context,0, Intent(ACTION_PREVIOUS),PendingIntent.FLAG_IMMUTABLE)
                    )
            )
            addAction(
                    NotificationCompat.Action(
                            R.drawable.outline_pause_24,
                            "pause",
                            PendingIntent.getBroadcast(context,
                                    0, Intent(ACTION_PAUSE), PendingIntent.FLAG_IMMUTABLE)
            )
            )
            addAction(
                    NotificationCompat.Action(
                            R.drawable.outline_skip_next_24,
                            "next",
                            PendingIntent.getBroadcast(context,
                                    0, Intent(ACTION_NEXT), PendingIntent.FLAG_IMMUTABLE)
                    )
            )

            setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mSession?.sessionToken)
                            .setShowActionsInCompactView(0,1,2)
                            .setShowCancelButton(true)
                            .setCancelButtonIntent(
                                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                                            context,
                                            PlaybackStateCompat.ACTION_STOP
                                    )
                            )
            )
        }
        startForeground(1,buidler.build())
    }
}




