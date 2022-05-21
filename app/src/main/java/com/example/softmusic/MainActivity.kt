package com.example.softmusic

import android.annotation.SuppressLint
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.softmusic.databinding.ActivityMainBinding
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.bottomSheet.SongBottomSheet
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
    var t : UpdatePositionThread ?= null
    private val TAG = "MainActivity"
    private lateinit var activityMainBinding: ActivityMainBinding
    lateinit var songBottomSheet: SongBottomSheet

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        val navController: NavController = findNavController(R.id.nav_host_fragment_activity_main)
        val navigationView: BottomNavigationView = activityMainBinding.navView
        songBottomSheet = SongBottomSheet(mainViewModel.currentMusicId.value?:0L)
        setupWithNavController(navigationView, navController)
        navController.addOnDestinationChangedListener { _, _, _ ->
            activityMainBinding.appBar.title = navController.currentDestination?.label
        }

        activityMainBinding.appBar.setOnMenuItemClickListener{menuItem ->
            when(menuItem.itemId){
                R.id.song_menu -> {
                    songBottomSheet.show(supportFragmentManager, SongBottomSheet.TAG)
                    true
                }
                else -> {
                    true
                }
            }
        }

        mainViewModel.haveMusicFlag = false

        mainViewModel.currentMusicId.observe(this) {
            songBottomSheet = SongBottomSheet(it)
        }

        mainViewModel.initFlag.observe(this){
            if (it == true){
                mainViewModel.initFlag.value = false
                mController.transportControls.play()
                t = UpdatePositionThread()
                t?.start()
            }
        }
        mainViewModel.currentId.observe(this) {

            with(mainViewModel){
                currentPlayMode.value = MediaPlaybackService.DEFAULT
                nowPlayList.value = DataBaseUtils.getPlayListsWithSongsById(it[1])
                rawPlayList.value = mainViewModel.nowPlayList.value
            }

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
                mBrowser = MediaBrowserCompat(this, ComponentName(this, MediaPlaybackService::class.java),
                    mBrowserConnectionCallback, bundle)
                mBrowser.connect()
            } else {
                mController.transportControls?.sendCustomAction(MediaPlaybackService.CHANGE_LIST, bundle)
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

            mainViewModel.nowPlayList.value = DataBaseUtils.getPlayListsWithSongsById(kv.decodeLong("musicSongListId"))
            mainViewModel.rawPlayList.value = mainViewModel.nowPlayList.value

            if (kv.decodeLong("musicSongListId") == 1L) {
                mainViewModel.likeFlag.value = true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        if (mainViewModel.haveMusicFlag) {
            if (!mBrowser.isConnected) {
                mBrowser.connect()
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
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
        Log.d(TAG, "onStop")
        // TODO MMKV save the position
        val kv = MMKV.defaultMMKV()
        mainViewModel.currentMusicId.value?.let { kv.encode("musicSongId", it) }
        mainViewModel.currentId.value?.get(1)?.let { kv.encode("musicSongListId", it) }
    }


    private val mBrowserConnectionCallback: MediaBrowserCompat.ConnectionCallback =
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                Log.d(TAG, "onConnected")
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
            override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
                super.onChildrenLoaded(parentId, children)
                mainViewModel.duration.value = mController.metadata
                    ?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.toInt()

                val list = ArrayList<MusicSong>()

                for (i in children){
                    list.add(DataBaseUtils.getMusicSongById(i.mediaId?.toLong()!!))
                }
                mainViewModel.currentPlayList.value = list
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
                        if (mainViewModel.autoChangeFlag) {
                            t?.interrupt()
                            mainViewModel.autoChangeFlag = false
                            t = UpdatePositionThread()
                            t?.start()
                        }
                        mController.transportControls?.play()
                    }
                    PlaybackStateCompat.STATE_NONE -> {
                    }
                    PlaybackStateCompat.STATE_PLAYING -> {
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                    }
                }
                mainViewModel.position.value = (state?.position?.div(1000))?.toInt()
            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
                with(mainViewModel) {
                    duration.value = metadata?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.toInt()
                    currentTitle.value = metadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                    currentImageUri.value = DataBaseUtils.getImageUri(metadata?.getString(METADATA_KEY_MEDIA_ID)!!.toLong()
                    )
                    currentArtist.value = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                    currentMusicId.value = metadata.getString(METADATA_KEY_MEDIA_ID).toLong()
                }
                if (mController.playbackState.state == PlaybackStateCompat.STATE_NONE){
                    mainViewModel.initFlag.value = true
                }
            }
        }

    inner class UpdatePositionThread : Thread() {
        override fun run() {
            super.run()
            var flag = true
            while (!interrupted()) {
                if (mController.playbackState.state == PlaybackStateCompat.STATE_PLAYING && flag && !mainViewModel.touchFlag.value!!) {
                    mainViewModel.position.postValue((mController.playbackState.position / 1000).toInt())
                    if (mainViewModel.position.value!! >= (mainViewModel.duration.value!!.div(1000)) ){
                        mController.transportControls?.skipToNext()
                        flag = false
                        mainViewModel.autoChangeFlag = true
                    }
                }
            }
        }
    }
}