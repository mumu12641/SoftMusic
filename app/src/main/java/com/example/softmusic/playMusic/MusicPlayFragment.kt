package com.example.softmusic.playMusic

import android.annotation.SuppressLint
import android.content.ComponentName
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.softmusic.MainActivity
import com.example.softmusic.R
import com.example.softmusic.databinding.FragmentMusicPlayBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class MusicPlayFragment : Fragment(), SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private lateinit var musicPlayViewModel: MusicPlayViewModel

    private lateinit var mBrowser: MediaBrowserCompat
    private lateinit var mController: MediaControllerCompat
    private val TAG = "MediaPlayer"
    private lateinit var _binding: FragmentMusicPlayBinding
    private val binding get() = _binding

    private var mUpdateProgressThread:updateProgressThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = Bundle()
        if (arguments != null){
            bundle.putString("songListTitle", requireArguments().getString("songListTitle"))
            bundle.putString("songTitle", requireArguments().getString("songTitle"))
        }
        mBrowser = MediaBrowserCompat(
            requireContext(),
            ComponentName(requireContext(), MediaPlaybackService::class.java),  //绑定服务
            mBrowserConnectionCallback,  // 设置回调
            bundle
        )
        musicPlayViewModel = ViewModelProvider(requireActivity())[MusicPlayViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        if (!mBrowser.isConnected) {
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
        mBrowser.disconnect()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicPlayBinding.inflate(inflater, container, false)
        binding.seekBar.setOnSeekBarChangeListener(this)
        binding.playsong.setOnClickListener(this)
        binding.nextsong.setOnClickListener(this)
        binding.lastsong.setOnClickListener(this)

        val dateFormat = SimpleDateFormat("mm:ss", Locale.CHINA)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+00:00")
        musicPlayViewModel.duration.observe(viewLifecycleOwner) { duration ->
            binding.seekBar.max = duration

            binding.durationTime.text = dateFormat.format(Date(duration.toLong()))
        }
        musicPlayViewModel.nowPos.observe(viewLifecycleOwner) { now ->
            binding.seekBar.progress = now * 1000
            binding.nowTime.text = dateFormat.format(Date((now * 1000).toLong()))
        }
        musicPlayViewModel.nextFlag.observe(viewLifecycleOwner) {
            if (it){
                binding.nextsong.performClick()
                musicPlayViewModel.setFlag(false)
            }
        }

        return binding.root
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}
    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        val pos: Int = seekBar.progress
//        musicPlayViewModel.nowPos = pos / 1000
        musicPlayViewModel.setNow(pos / 1000)
        musicPlayViewModel.lastPos = -1
        mController.transportControls.seekTo(pos.toLong())
        mController.transportControls.play()
        binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
    }

    @SuppressLint("SwitchIntDef", "NonConstantResourceId")
    override fun onClick(view: View) {
        when(view.id){
            R.id.playsong->{
                if (mController != null) {
                    when (mController.playbackState.state) {
                        PlaybackStateCompat.STATE_PLAYING -> {
                            mController.transportControls.pause()
                            musicPlayViewModel.lastPos = musicPlayViewModel.nowPos.value!!
                        }
                        PlaybackStateCompat.STATE_PAUSED -> {
                            mController.transportControls.play()
                            musicPlayViewModel.lastPos = -1
                        }
                        PlaybackStateCompat.STATE_NONE -> {
                            mController.transportControls.play()
                            mUpdateProgressThread = updateProgressThread()
                            mUpdateProgressThread?.start()
                        }
                    }
                }

            }
            R.id.nextsong->{
                if (arguments!=null) {
                    mController.transportControls.skipToNext()
                }
            }
            R.id.lastsong->{
                if (arguments!=null) {
                    mController.transportControls.skipToPrevious()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setTitle("Play")
    }

    // 连接状态的回调接口，连接成功的时候会调用这里的onConnected方法
    private val mBrowserConnectionCallback: MediaBrowserCompat.ConnectionCallback =
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()
                Log.d(TAG, "onConnected")
                if (mBrowser.isConnected) {
                    // 如果连接成功 这里的mediaId就为MediaBrowserService.onGetRoot的返回值
                    val mediaId: String = mBrowser.root
                    mBrowser.unsubscribe(mediaId)
                    mBrowser.subscribe(
                        mediaId,
                        mBrowserSubscriptionCallback // 这里是订阅回调接口，当Service读取数据后会把数据发送回来，调用这个回调接口
                    )
                    mController =
                        MediaControllerCompat(requireContext(), mBrowser.sessionToken)
                    // 给Controller注册回调
                    mController.registerCallback(mMediaControllerCallback)
                }
                if (mController.metadata!=null) {
                    val metadataCompat: MediaMetadataCompat = mController.metadata
                    updateDuration(metadataCompat)
                }
            }

        }

    // 向媒体浏览器服务(MediaBrowserService)发起数据订阅请求的回调接口
    private val mBrowserSubscriptionCallback: MediaBrowserCompat.SubscriptionCallback =
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: List<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                Log.d(TAG, "onChildrenLoaded")
                // children 就是Service返回回来的数据
                if (children.isEmpty()){
                    Toast.makeText(requireContext(),"你的播放列表中还什么都没有～",Toast.LENGTH_LONG).show()
                    return
                }
                for (item in children) {
                    Log.d(
                        TAG,
                        Objects.requireNonNull<CharSequence>(item.description.title)
                            .toString()
                    )
                }
                if (mController.metadata!=null) {
                    val metadataCompat: MediaMetadataCompat = mController.metadata
                    updateDuration(metadataCompat)
                }
            }
        }

    // 这个是Controller 的回调，通过这个回调来更新UI
    private val mMediaControllerCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            @SuppressLint("SwitchIntDef")
            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                super.onPlaybackStateChanged(state)
                when (state.state) {
                    PlaybackStateCompat.STATE_NONE -> {
                        updateProgress()
                        binding.playsong.performClick()
                    }

                    PlaybackStateCompat.STATE_PAUSED -> {
                        binding.playsong.setBackgroundResource(R.drawable.outline_play_arrow_24)
                    }
                    PlaybackStateCompat.STATE_PLAYING -> {
                        binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
                    }
                }
                if (mController.metadata!=null) {
                    val metadataCompat: MediaMetadataCompat = mController.metadata
                    updateDuration(metadataCompat)
                }
            }

        }

    private fun updateDuration(metadataCompat: MediaMetadataCompat?) {
        if (metadataCompat != null) {
            val duration = metadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt()
            Log.d(TAG, "updateDuration: $duration")
            if (duration > 0) {
                musicPlayViewModel.setAllDuration(duration)
            }
        } else {
            Log.d(TAG, "updateDuration: null")
        }
    }

    private inner class updateProgressThread : Thread() {
        override fun run() {
            super.run()
            while (binding.seekBar.progress < binding.seekBar.max) {
                if (musicPlayViewModel.nowPos.value == musicPlayViewModel.lastPos) {
                    continue
                }
                musicPlayViewModel.postNow(musicPlayViewModel.nowPos.value!!.plus(1))
                SystemClock.sleep(1000)
            }
            Log.d(TAG, "run: song play done" )
            musicPlayViewModel.postFlag(true)
        }
    }

    private fun updateProgress(){
        mUpdateProgressThread = null
        musicPlayViewModel.lastPos = -1
        musicPlayViewModel.setNow(0)
    }
}

