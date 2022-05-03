package com.example.softmusic

import android.annotation.SuppressLint
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
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


        mainViewModel.nowId.observe(this){
            // 直接重新连接？？
            // TODO 还可以优化
            val bundle = Bundle()
            bundle.apply {
                putLong("musicSongId",it[0])
                putLong("musicSongListId",it[1])
            }
            MediaControllerCompat.getMediaController(this)?.unregisterCallback(mMediaControllerCallback)
            if (mainViewModel.haveMusicFlag){
                mBrowser.disconnect()
            }
            mBrowser = MediaBrowserCompat(
                this,
                ComponentName(this, MediaPlaybackService::class.java),  //绑定服务
                mBrowserConnectionCallback,  // 设置回调
                bundle
            )
            mBrowser.connect()
            Toast.makeText(this,"成功替换播放列表",Toast.LENGTH_LONG).show()
            mainViewModel.haveMusicFlag = true
        }

        // TODO Test Bundle
        val bundle = Bundle()
        val kv = MMKV.defaultMMKV()
        if (!kv.containsKey("musicSongListId") && !kv.containsKey("musicSongId")){
            mainViewModel.haveMusicFlag = false
        } else {
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

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
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
                Log.d(TAG, "onChildrenLoaded: " + mainViewModel.duration.value)
                mainViewModel.duration.value = mController?.metadata
                    ?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.toInt()
                Log.d(TAG, "onChildrenLoaded: " + mainViewModel.duration.value)
                thread = UpdateProcessThread()
            }
        }
    private val mMediaControllerCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            @SuppressLint("SwitchIntDef")
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
                when(state?.state){
                    PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                        Log.d(TAG, "onPlaybackStateChanged: state next song")
                        mainViewModel.nowProcess.value = 0
                        mainViewModel.lastProcess.value = -1
//                        thread = null
//                        thread = UpdateProcessThread()
                        mainViewModel.changeFlag.value = true
                    }
                    PlaybackStateCompat.STATE_NONE -> {
                        Log.d(TAG, "onPlaybackStateChanged: state NONE")
                        mainViewModel.nowProcess.value = 0
                        mainViewModel.lastProcess.value = -1

                        mainViewModel.initFlag.value = true
                    }
                }
                mainViewModel.duration.value = mController?.metadata
                    ?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)?.toInt()
                mainViewModel.nowTitle.value = mController?.metadata
                    ?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
            }
        }

    inner class UpdateProcessThread:Thread(){
        override fun run() {
            super.run()
            while (mainViewModel.nowProcess.value!! < (mainViewModel.duration.value!!.div(1000))){
                if (mainViewModel.nowProcess.value == mainViewModel.lastProcess.value){
                    continue
                }
                mainViewModel.nowProcess.postValue(mainViewModel.nowProcess.value!!.plus(1))
                SystemClock.sleep(1000)
                if (mainViewModel.nowProcess.value!! == (mainViewModel.duration.value!!.div(1000))){
                    Log.d(TAG, "run: 播放完毕 下一首")
                    mController?.transportControls?.skipToNext()
                    Log.d(TAG, "run: ok")
                }
            }
        }
    }

    fun setTitle(title: String?) {
        activityMainBinding.appBar.title = title
    }
}