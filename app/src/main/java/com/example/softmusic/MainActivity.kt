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
    var mController: MediaControllerCompat? = null
    lateinit var mainViewModel: MainViewModel

    var thread:UpdateProcessThread? = null

    private val TAG = "MainActivity"

    private lateinit var activityMainBinding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        val navController: NavController =
            findNavController(R.id.nav_host_fragment_activity_main)
        val navigationView: BottomNavigationView = activityMainBinding.navView
        setupWithNavController(navigationView, navController)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        mainViewModel.nowId.observe(this){ it ->
            mainViewModel.nowMusicRecordImageList.value =
                DataBaseUtils.getPlayListsWithSongsById(it[1]).map{it.songAlbum}
            val bundle = Bundle()
            bundle.apply {
                putLong("musicSongId",it[0])
                putLong("musicSongListId",it[1])
            }
            if (!mainViewModel.haveMusicFlag) {
                MediaControllerCompat.getMediaController(this)?.unregisterCallback(mMediaControllerCallback)
                if (mainViewModel.haveMusicFlag) {
                    mBrowser.disconnect()
                }
                mBrowser = MediaBrowserCompat(
                        this,
                        ComponentName(this, MediaPlaybackService::class.java),  //绑定服务
                        mBrowserConnectionCallback,  // 设置回调
                        bundle
                )
                mBrowser.connect()
            } else {
                mController?.transportControls?.sendCustomAction("1",bundle)
                thread?.interrupt()
                thread = UpdateProcessThread()
            }
            Toast.makeText(this,"成功替换播放列表",Toast.LENGTH_LONG).show()
            Log.d(TAG, "onCreate: change list")
            mainViewModel.haveMusicFlag = true

            if (it[1] == 1L){
                mainViewModel.likeFlag.value = true
            }

        }

        val bundle = Bundle()
        val kv = MMKV.defaultMMKV()
        if (!kv.containsKey("musicSongListId") && !kv.containsKey("musicSongId")){
            mainViewModel.haveMusicFlag = false
        } else {
            Log.d(TAG, "onCreate: mmkv true")
            mainViewModel.nowMusicRecordImageList.value =
                DataBaseUtils.getPlayListsWithSongsById(kv.decodeLong("musicSongListId")).map{it.songAlbum}
            mainViewModel.haveMusicFlag = true
            bundle.apply {
                putLong("musicSongListId", kv.decodeLong("musicSongListId"))
                putLong("musicSongId", kv.decodeLong("musicSongId"))
            }
            mBrowser = MediaBrowserCompat(
                this,
                ComponentName(this, MediaPlaybackService::class.java),  //绑定服务
                mBrowserConnectionCallback,  // 设置回调
                bundle
            )
            if (kv.decodeLong("musicSongListId") == 1L){
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

    private val mBrowserConnectionCallback:MediaBrowserCompat.ConnectionCallback =
        object : MediaBrowserCompat.ConnectionCallback(){
            override fun onConnected() {
                super.onConnected()
                Log.d(TAG, "onConnected:")
                if (mBrowser.isConnected){
                    val mediaId :String = mBrowser.root
                    mBrowser.unsubscribe(mediaId)
                    mBrowser.subscribe(mediaId,mBrowserSubscriptionCallback)
                    mController = MediaControllerCompat(this@MainActivity,mBrowser.sessionToken)
                    mController!!.registerCallback(mMediaControllerCallback)
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
                mainViewModel.duration.value = mController?.metadata
                    ?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.toInt()
                Log.d(TAG, "onChildrenLoaded: " + mainViewModel.duration.value)
                thread?.interrupt()
                thread = UpdateProcessThread()
            }
        }
    private val mMediaControllerCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            @SuppressLint("SwitchIntDef")
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)

                mainViewModel.playbackState.value = state

                when(state?.state){
                    PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                        mainViewModel.nowProcess.value = 0
                        mainViewModel.lastProcess.value = -1
                        if (mainViewModel.autoChangeFlag){
                            thread?.interrupt()
                            mainViewModel.autoChangeFlag = false
                            mainViewModel.nowProcess.value = -1
                            mainViewModel.lastProcess.value = -2
                            thread = UpdateProcessThread()
                            thread?.start()
                        }
//                        mainViewModel.changeFlag.value = true
                        mController?.transportControls?.play()
                    }
                    PlaybackStateCompat.STATE_NONE -> {
                        mainViewModel.nowProcess.value = 0
                        mainViewModel.lastProcess.value = -1
                        mainViewModel.initFlag.value = true
                    }
                    PlaybackStateCompat.STATE_PLAYING -> {
                        Log.d(TAG, "onPlaybackStateChanged: playing"
                                + ((SystemClock.elapsedRealtime() - state.lastPositionUpdateTime) + state.position))
                        mainViewModel.lastProcess.value = -1
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        mainViewModel.lastProcess.value = mainViewModel.nowProcess.value
                    }
                }

            }

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                super.onMetadataChanged(metadata)
                mainViewModel.duration.value = metadata
                        ?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.toInt()
                mainViewModel.nowTitle.value = metadata
                        ?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                mainViewModel.nowImageUri.value = DataBaseUtils.getImageUri(
                        metadata?.getString(METADATA_KEY_MEDIA_ID)!!.toLong())
            }
        }

    inner class UpdateProcessThread:Thread(){
        override fun run() {
            super.run()
            while (!interrupted() && mainViewModel.nowProcess.value!! < (mainViewModel.duration.value!!.div(1000))){
                if (mainViewModel.nowProcess.value == mainViewModel.lastProcess.value){
                    continue
                }
                mainViewModel.nowProcess.postValue(mainViewModel.nowProcess.value!!.plus(1))
                SystemClock.sleep(1000)
                if (mainViewModel.nowProcess.value!! == (mainViewModel.duration.value!!.div(1000))){
                    mController?.transportControls?.skipToNext()
                    mainViewModel.autoChangeFlag = true
                }
            }
        }
    }

    fun setTitle(title: String?) {
        activityMainBinding.appBar.title = title
    }
}