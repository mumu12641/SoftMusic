package com.example.softmusic.playMusic

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
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
import com.example.softmusic.R
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.room.DataBaseUtils
import com.google.android.exoplayer2.ExoPlayer
import com.tencent.mmkv.MMKV
import java.util.*


class MediaPlaybackService : MediaBrowserServiceCompat() {
    private var mSession: MediaSessionCompat? = null
    private var mPlaybackState: PlaybackStateCompat? = null
    private var mExoPlayer: ExoPlayer? = null
    private var mMediaPlayer: MediaPlayer? = null

    private var songTitle:String ?= null
    private var songListTitle:String ?= null

    private var musicSongId:Long = 0
    private var musicSongListId:Long = 0

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

        val kv = MMKV.defaultMMKV()
        kv.encode("musicSongId", list?.get(nowNum)?.musicSongId!!)
        kv.encode("musicSongListId",musicSongListId)

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
        // TODO get the bundle and set the playlist

        val kv = MMKV.defaultMMKV()
//        songTitle = rootHints?.getString("songTitle")
//        songListTitle = rootHints?.getString("songListTitle")
        // val c = a?:b if a == null,c = b
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


        // TODO 上次app播放的地方
        val kv = MMKV.defaultMMKV()
        if (kv.decodeString("songTitle") == null){
            Log.d(TAG, "onLoadChildren: mmkv null" )
        }
        val musicSong = DataBaseUtils.getMusicSongById(musicSongId)
        val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()
//        list = kv.decodeString("songListTitle")?.let { DataBaseUtils.getPlayListsWithSongsByKey(it) }
        Log.d(TAG, "onLoadChildren: $musicSongId")
        list = DataBaseUtils.getPlayListsWithSongsById(musicSongListId)
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
//                var mediaItem = MediaItem.fromUri(i.mediaFileUri)
//                mExoPlayer?.addMediaItem(mediaItem)
//                mSession!!.isActive = true
//                mSession!!.setMetadata(metadata)
        }
        mMediaPlayer?.setDataSource(musicSong.mediaFileUri)
        mMediaPlayer?.prepareAsync()
//        mExoPlayer?.prepare()
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "" + R.raw.jay)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicSong.songTitle)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, musicSong.duration.toLong())
            .build()
        //向Browser发送数据
        mSession!!.isActive = true
        mSession!!.setMetadata(metadata)
//        Log.d(TAG, "onLoadChildren: " + mMediaPlayer!!.duration)
        result.sendResult(mediaItems)
    }

    // 这个接口是响应控制器指令的回调
    private val mSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                Log.d(TAG, "onPlay")
                if (mPlaybackState!!.state == PlaybackStateCompat.STATE_PAUSED || mPlaybackState!!.state == PlaybackStateCompat.STATE_NONE) {
//                mExoPlayer?.play()
                    mMediaPlayer!!.start()
                    mPlaybackState = PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
                        .build()
                    mSession!!.setPlaybackState(mPlaybackState)

                    val controller = mSession!!.controller
                    val metaData = controller.metadata
                    val des = metaData.description

                    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    @SuppressLint("WrongConstant") val builder: NotificationCompat.Builder
                    val channelId: String =
                        java.lang.String.valueOf(Random().nextInt()) //自己生成的用于通知栏的channelId，高版本必备

                    val mChannel: NotificationChannel?
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mChannel = NotificationChannel(
                            channelId,
                            "name",
                            NotificationManager.IMPORTANCE_HIGH
                        )
                        mChannel.enableVibration(true)
                        mChannel.vibrationPattern =
                            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                        manager.createNotificationChannel(mChannel)
                        builder = NotificationCompat.Builder(applicationContext, channelId).apply {

                            setContentTitle(des.title)
                            setContentText(des.subtitle)
                            setSubText(des.description)
                            setLargeIcon(des.iconBitmap)

                            // Enable launching the player by clicking the notification
                            setContentIntent(controller.sessionActivity)
                            // Stop the service when the notification is swiped away
                            setDeleteIntent(
                                MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    applicationContext,
                                    PlaybackStateCompat.ACTION_STOP
                                )
                            )


                            setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                            setSmallIcon(R.drawable.ic_launcher_foreground)

                            addAction(
                                NotificationCompat.Action(
                                    R.drawable.outline_pause_24,
                                    "暂停",
                                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                                        applicationContext,
                                        PlaybackStateCompat.ACTION_PAUSE
                                    )
                                )
                            )

                            // Take advantage of MediaStyle features
                            setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(mSession!!.sessionToken)
                                .setShowActionsInCompactView(0)

                                // Add a cancel button
                                .setShowCancelButton(true)
                                .setCancelButtonIntent(
                                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                                        applicationContext,
                                        PlaybackStateCompat.ACTION_STOP
                                    )
                                )
                            )
                        }
                    } else {
                        builder = NotificationCompat.Builder(applicationContext)
                    }
//                    manager.notify(1,notification)
                    startForeground(1, builder.build())

                }
            }

            override fun onPause() {
                super.onPause()
                Log.d(TAG, "onPause")
                if (mPlaybackState!!.state == PlaybackStateCompat.STATE_PLAYING) {
//                mExoPlayer?.pause()
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
//                            mExoPlayer?.seekTo(pos);
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
//                mExoPlayer?.seekToNext()
//                mPlaybackState = PlaybackStateCompat.Builder()
//                    .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
//                    .build()
//                mSession!!.setPlaybackState(mPlaybackState)
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                nowNum -= 1
                if (nowNum < 0){
                    nowNum = list?.size?.minus(1)!!
                }
                changeMusicSong(song = list?.get(nowNum)!!)
//                mExoPlayer?.seekToPrevious()
//                mPlaybackState = PlaybackStateCompat.Builder()
//                    .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
//                    .build()
//                mSession!!.setPlaybackState(mPlaybackState)
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




