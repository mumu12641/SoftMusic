package com.example.softmusic.playMusic

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.example.softmusic.BaseApplication.Companion.context
import com.example.softmusic.R
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.room.DataBaseUtils
import com.google.android.exoplayer2.ExoPlayer
import com.tencent.mmkv.MMKV
import java.util.*


class MediaPlaybackService : MediaBrowserServiceCompat() {
    private var mSession: MediaSessionCompat? = null
    private var mPlaybackState: PlaybackStateCompat? = null
    private lateinit var mMediaPlayer: MediaPlayer

    private lateinit var mExoPlayer:ExoPlayer


    private var musicSongId:Long = 0
    private var musicSongListId:Long = 0

    private var mode = DEFAULT

    private var playNum = 0
    private var nowNum = 0
    private var list:List<MusicSong>? = null

    private val TAG = "MediaPlaybackService"


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

        mMediaPlayer = MediaPlayer()
        mExoPlayer = ExoPlayer.Builder(this).build()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        val kv = MMKV.defaultMMKV()
        kv.encode("musicSongId", list?.get(nowNum)?.musicSongId!!)
        kv.encode("musicSongListId",musicSongListId)
        mMediaPlayer.release()
        mExoPlayer.release()
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
        loadMusic()
        for (i in list!!){
            val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + i.musicSongId.toString())
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
                    createNotification()
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
                when(mode){
                    DEFAULT -> {
                        nowNum += 1
                    }
                    SHUFFLE -> {
                        nowNum = (0 until list?.size!!).random()
                    }
                    REPEAT_ONE -> {
                    }
                }
                Log.d(TAG, "onSkipToNext: $nowNum")
                if (nowNum == list?.size!!){
                    nowNum = 0
                }
                changeMusicSong(song = list?.get(nowNum)!!)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                when(mode){
                    DEFAULT -> {
                        nowNum -= 1
                    }
                    SHUFFLE -> {
                        nowNum = (0 until list?.size!!).random()
                    }
                    REPEAT_ONE -> {
                    }
                }
                if (nowNum < 0){
                    nowNum = list?.size?.minus(1)!!
                }
                changeMusicSong(song = list?.get(nowNum)!!)
            }

            override fun onCustomAction(action: String?, extras: Bundle?) {
                super.onCustomAction(action, extras)
                if (action == "0"){
                    // 切换播放顺序
                    mode = extras?.getInt("order")!!
                } else if (action == "1"){
                    // TODO 更换曲目
                    musicSongId = extras?.getLong("musicSongId")!!
                    musicSongListId = extras.getLong("musicSongListId")
                    Log.d(TAG, "onCustomAction: 1")
                    mMediaPlayer.stop()
                    mMediaPlayer.reset()
                    loadMusic()
                }
            }
        }


    companion object {
        private const val MY_MEDIA_ROOT_ID = "media_root_id"

        const val SHUFFLE = 1
        const val DEFAULT = 0
        const val REPEAT_ONE = 2

    }

    private fun changeMusicSong(song: MusicSong){
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + song.musicSongId.toString())
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

    private fun loadMusic(){
        val musicSong = DataBaseUtils.getMusicSongById(musicSongId)
        list = DataBaseUtils.getPlayListsWithSongsById(musicSongListId)
        nowNum = list!!.indexOf(musicSong)
        playNum = list!!.size



        mMediaPlayer.setDataSource(musicSong.mediaFileUri)
        mMediaPlayer.prepareAsync()
        val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, musicSong.musicSongId.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicSong.songTitle)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musicSong.duration.toLong())
                .build()
        mSession!!.isActive = true
        mSession!!.setMetadata(metadata)
        mPlaybackState = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                .build()
        mSession!!.setPlaybackState(mPlaybackState)
//        mMediaPlayer.start()
    }

    private fun createNotification(){
        val controller = mSession?.controller
        val mediaMetadata = controller?.metadata
        val description = mediaMetadata?.description!!
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        @SuppressLint("WrongConstant") val notification: Notification.Builder
        val channelId: String =
            java.lang.String.valueOf(Random().nextInt())
        var mChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = NotificationChannel(channelId, "name", NotificationManager.IMPORTANCE_HIGH)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            manager.createNotificationChannel(mChannel)
            val buidler = NotificationCompat.Builder(context, channelId).apply {
                setContentTitle(description.title)
                setContentText(description.subtitle)
                setSubText(description.description)
                setLargeIcon(description.iconBitmap)

                setContentIntent(controller.sessionActivity)

                // Stop the service when the notification is swiped away
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
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        )
                    )
                )
                addAction(
                    NotificationCompat.Action(
                        R.drawable.outline_pause_24,
                        "pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_PAUSE
                        )
                    )
                )
                addAction(
                    NotificationCompat.Action(
                        R.drawable.outline_skip_next_24,
                        "next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT
//                            KeyEvent.KEYCODE_MEDIA_PAUSE.toLong()
                        )
                    )
                )

                setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mSession?.sessionToken)
                    .setShowActionsInCompactView(0)
                    // Add a cancel button
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    )
                )
            }
//            startForeground(1,buidler.build())
            (( getSystemService(Context.NOTIFICATION_SERVICE)) as NotificationManager).notify(1, buidler.build());
        }
    }
}




