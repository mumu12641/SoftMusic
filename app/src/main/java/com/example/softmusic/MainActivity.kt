package com.example.softmusic

import android.annotation.SuppressLint
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.softmusic.databinding.ActivityMainBinding
import com.example.softmusic.playMusic.MediaPlaybackService
import com.example.softmusic.room.DataBaseUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tencent.mmkv.MMKV

class MainActivity : AppCompatActivity() {

    lateinit var mBrowser: MediaBrowserCompat
    lateinit var mController: MediaControllerCompat
    val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this@MainActivity)[MainViewModel::class.java]
    }
    var thread: UpdateProcessThread? = null
    private val TAG = "MainActivity"
    private lateinit var activityMainBinding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        val navController: NavController = findNavController(R.id.nav_host_fragment_activity_main)
        val navigationView: BottomNavigationView = activityMainBinding.navView
        setupWithNavController(navigationView, navController)

        navController.addOnDestinationChangedListener { _, _, _ ->
            activityMainBinding.appBar.title = navController.currentDestination?.label
        }


        mainViewModel.initFlag.observe(this){
            if (it == true){
                mainViewModel.initFlag.value = false
                mController.transportControls.play()
                Log.d(TAG, "onCreate: " + mainViewModel.duration.value)
                Log.d(TAG, "onCreate: " + mainViewModel.currentProgress.value)
                thread?.interrupt()
                thread = null
                thread = UpdateProcessThread()
                thread?.start()
            }
        }
        mainViewModel.currentId.observe(this) { it ->
            mainViewModel.nowMusicRecordImageList.value =
                DataBaseUtils.getPlayListsWithSongsById(it[1]).map { it.songAlbum }
            mainViewModel.rawMusicRecordImageList.value = mainViewModel.nowMusicRecordImageList.value
            val bundle = Bundle()
            bundle.apply {
                putLong("musicSongId", it[0])
                putLong("musicSongListId", it[1])
            }
            if (!mainViewModel.haveMusicFlag) {
                MediaControllerCompat.getMediaController(this)?.unregisterCallback(mMediaControllerCallback)
                if (mainViewModel.haveMusicFlag) {
                    mBrowser.disconnect()
                }
                mainViewModel.haveMusicFlag = true
                mBrowser = MediaBrowserCompat(this, ComponentName(this,
                    MediaPlaybackService::class.java),
                    mBrowserConnectionCallback,
                    bundle
                )
                mBrowser.connect()
            } else {
                mController.transportControls?.sendCustomAction(
                    MediaPlaybackService.CHANGE_LIST,
                    bundle
                )
                thread?.interrupt()
                thread = UpdateProcessThread()
            }
            mainViewModel.haveMusicFlag = true

            if (it[1] == 1L) {
                mainViewModel.likeFlag.value = true
            }

        }

        val kv = MMKV.defaultMMKV()
        if (!kv.containsKey("musicSongListId") && !kv.containsKey("musicSongId")) {
            mainViewModel.haveMusicFlag = false
        } else {
            mainViewModel.currentId.value = listOf(kv.decodeLong("musicSongId"),kv.decodeLong("musicSongListId"))
            mainViewModel.currentMusicId.value = kv.decodeLong("musicSongId")
            mainViewModel.nowMusicRecordImageList.value =
                DataBaseUtils.getPlayListsWithSongsById(kv.decodeLong("musicSongListId"))
                    .map { it.songAlbum }
            mainViewModel.rawMusicRecordImageList.value = mainViewModel.nowMusicRecordImageList.value
            if (kv.decodeLong("musicSongListId") == 1L) {
                mainViewModel.likeFlag.value = true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (mainViewModel.haveMusicFlag) {
            if (!mBrowser.isConnected) {
                mBrowser.connect()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mainViewModel.haveMusicFlag) {
            if (mBrowser.isConnected) {
                MediaControllerCompat.getMediaController(this)
                    ?.unregisterCallback(mMediaControllerCallback)
                mBrowser.disconnect()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
        // TODO mmkv
    }


    private val mBrowserConnectionCallback: MediaBrowserCompat.ConnectionCallback =
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()
                if (mBrowser.isConnected) {
                    val mediaId: String = mBrowser.root
                    mBrowser.unsubscribe(mediaId)
                    mBrowser.subscribe(mediaId, mBrowserSubscriptionCallback)
                    mController = MediaControllerCompat(this@MainActivity, mBrowser.sessionToken)
                    mController.registerCallback(mMediaControllerCallback)
                }
            }
        }
    private val mBrowserSubscriptionCallback: MediaBrowserCompat.SubscriptionCallback =
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                mainViewModel.duration.value = mController.metadata
                    ?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.toInt()
//                thread?.interrupt()
//                thread = UpdateProcessThread()
            }
        }
    private val mMediaControllerCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            @SuppressLint("SwitchIntDef")
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)

                mainViewModel.playbackState.value = state

                when (state?.state) {
                    PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                        mainViewModel.currentProgress.value = 0
                        mainViewModel.lastProgress.value = -1
                        if (mainViewModel.autoChangeFlag) {
                            thread?.interrupt()
                            mainViewModel.autoChangeFlag = false
                            mainViewModel.currentProgress.value = -1
                            mainViewModel.lastProgress.value = -2
                            thread = UpdateProcessThread()
                            thread?.start()
                        }
                        mController.transportControls?.play()
                    }
                    PlaybackStateCompat.STATE_NONE -> {
                        mainViewModel.currentProgress.value = (state.position / 1000).toInt()
                        mainViewModel.lastProgress.value = -1
                    }
                    PlaybackStateCompat.STATE_PLAYING -> {
                        mainViewModel.lastProgress.value = -1
                        mainViewModel.currentProgress.value = (state.position / 1000).toInt()
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        mainViewModel.lastProgress.value = mainViewModel.currentProgress.value
                    }
                }

            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
                with(mainViewModel) {
                    duration.value = metadata
                        ?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.toInt()
                    currentTitle.value = metadata
                        ?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                    currentImageUri.value = DataBaseUtils.getImageUri(
                        metadata?.getString(METADATA_KEY_MEDIA_ID)!!.toLong()
                    )
                    currentArtist.value =
                        metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                    currentMusicId.value = metadata.getString(METADATA_KEY_MEDIA_ID).toLong()
                    Log.d(TAG, "onMetadataChanged: " + currentImageUri.value)
                }
                if (mController.playbackState.state == PlaybackStateCompat.STATE_NONE){
                    mainViewModel.currentProgress.value = 0
                    mainViewModel.lastProgress.value = -1
                    mainViewModel.initFlag.value = true
                }
            }
        }

    inner class UpdateProcessThread : Thread() {
        override fun run() {
            super.run()
            while (!interrupted() && mainViewModel.currentProgress.value!! < (mainViewModel.duration.value!!.div(1000))) {
                if (mainViewModel.currentProgress.value == mainViewModel.lastProgress.value) {
                    continue
                }
                mainViewModel.currentProgress.postValue(mainViewModel.currentProgress.value!!.plus(1))
                SystemClock.sleep(1000)
                if (mainViewModel.currentProgress.value!! >= (mainViewModel.duration.value!!.div(1000)) - 1) {
                    mController.transportControls?.skipToNext()
                    mainViewModel.autoChangeFlag = true
                }
            }
        }
    }
}