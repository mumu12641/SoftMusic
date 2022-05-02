package com.example.softmusic

import android.content.ComponentName
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.softmusic.databinding.ActivityMainBinding
import com.example.softmusic.playMusic.MediaPlaybackService
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var mBrowser: MediaBrowserCompat
    lateinit var mController: MediaControllerCompat
    lateinit var mainViewModel: MainViewModel

    var thread:UpdateProcessThread? = null

    private val TAG = "MainActivity"

    private lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        val navController: NavController =
            findNavController(R.id.nav_host_fragment_activity_main)
        val navigationView: BottomNavigationView = activityMainBinding.navView
        setupWithNavController(navigationView, navController)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // TODO Test Bundle
        val bundle = Bundle()
        bundle.apply {
            putLong("musicSongListId",1)
            putLong("musicSongId",1)
        }
        mBrowser = MediaBrowserCompat(
            this,
            ComponentName(this, MediaPlaybackService::class.java),  //绑定服务
            mBrowserConnectionCallback,  // 设置回调
            bundle
        )

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        if (!mBrowser.isConnected){
            mBrowser.connect()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(mMediaControllerCallback)
        mBrowser.disconnect()
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
                val list = ArrayList<String>()
                for (item in children){
                    // TODO 这里load到children的数据 应该放到ViewModel中去
                    Log.d(TAG, "onChildrenLoaded: " + item.description.title)
                    list.add(item.description.title.toString())
                }
                mainViewModel.loadChildren(list)
                mainViewModel.duration.value = mController.metadata
                    .getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt()
                thread = UpdateProcessThread()
            }
        }
    private val mMediaControllerCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
                when(state?.state){
                    PlaybackStateCompat.STATE_NONE -> {

                    }
                    PlaybackStateCompat.STATE_PLAYING -> {

                    }
                    PlaybackStateCompat.STATE_PAUSED -> {

                    }
                    PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                        Log.d(TAG, "onPlaybackStateChanged: state next")
                        mainViewModel.nowProcess.value = state.position.toInt()
                        mainViewModel.lastProcess.value = -1
                        thread = null
                    }
                    PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS -> {

                    }
                }
//                mainViewModel.nowProcess.value = state?.position?.toInt()
                mainViewModel.duration.value = mController.metadata
                    .getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt()
            }
        }

    inner class UpdateProcessThread:Thread(){
        // 更新总体process的线程
        override fun run() {
            super.run()
            while (mainViewModel.nowProcess.value!! < mainViewModel.duration.value!!){
                if (mainViewModel.nowProcess.value == mainViewModel.lastProcess.value){

                    continue
                }
                mainViewModel.nowProcess.postValue(mainViewModel.nowProcess.value!!.plus(1))
                SystemClock.sleep(1000)
            }
            Log.d(TAG, "run: 播放完毕")
        }
    }



    fun setTitle(title: String?) {
        activityMainBinding.appBar.title = title
    }
}