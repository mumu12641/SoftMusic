package com.example.softmusic.playMusic

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.example.softmusic.MainActivity
import com.example.softmusic.MainViewModel
import com.example.softmusic.R
import com.example.softmusic.databinding.FragmentMusicPlayBinding
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.room.DataBaseUtils
import java.text.SimpleDateFormat
import java.util.*

class MusicPlayFragment : Fragment(), SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private lateinit var musicPlayViewModel: MusicPlayViewModel
    private lateinit var mainViewModel : MainViewModel
    private var mController: MediaControllerCompat? = null
    private val TAG = "MusicPlayFragment"
    private lateinit var _binding: FragmentMusicPlayBinding
    private val binding get() = _binding
    private var repeatMode = MediaPlaybackService.DEFAULT

    private var currentPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicPlayViewModel = ViewModelProvider(requireActivity())[MusicPlayViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        val dateFormat = SimpleDateFormat("mm:ss", Locale.CHINA)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+00:00")
        mController = (requireActivity() as MainActivity).mController
        mainViewModel = (requireActivity() as MainActivity).mainViewModel
        _binding = FragmentMusicPlayBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(requireContext())
        val adapter = MusicRecordAdapter(listOf(),requireContext())
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(binding.snapRecyclerview)

        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.apply {
            seekBar.setOnSeekBarChangeListener(this@MusicPlayFragment)
            playsong.setOnClickListener(this@MusicPlayFragment)
            nextsong.setOnClickListener(this@MusicPlayFragment)
            lastsong.setOnClickListener(this@MusicPlayFragment)
            favoriteFlag.setOnClickListener(this@MusicPlayFragment)
            repeatMode.setOnClickListener(this@MusicPlayFragment)
            snapRecyclerview.layoutManager = layoutManager
            snapRecyclerview.adapter = adapter
            snapRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when(newState){
                        SCROLL_STATE_IDLE -> {
                            if (layoutManager.findFirstCompletelyVisibleItemPosition() == currentPosition + 1){
                                binding.nextsong.performClick()
                            }else if (layoutManager.findFirstCompletelyVisibleItemPosition() == currentPosition - 1){
                                binding.lastsong.performClick()
                            }
                            currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                        }
                    }
                }
            })
        }
        if (mController?.playbackState?.state == PlaybackStateCompat.STATE_PLAYING){
            binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
        }else{
            binding.playsong.setBackgroundResource(R.drawable.outline_play_arrow_24)
        }
        mainViewModel.apply {
            nowImageUri.observe(viewLifecycleOwner){
//                Glide.with(requireContext()).load(it).placeholder(R.drawable.music_note_150).into(binding.imageView2)
                currentPosition = mainViewModel.nowMusicRecordImageList.value!!.indexOf(it)
                binding.snapRecyclerview.scrollToPosition(currentPosition)
            }
            duration.observe(viewLifecycleOwner){
                binding.seekBar.max = it
                binding.durationTime.text = dateFormat.format(Date(it.toLong()))
            }

            nowProcess.observe(viewLifecycleOwner){
                binding.seekBar.progress = (it *1000)
                binding.nowTime.text = dateFormat.format(Date((it * 1000).toLong()))
            }

            nowTitle.observe(viewLifecycleOwner){
                binding.songTitle.text = it
            }

            changeFlag.observe(viewLifecycleOwner){
                if (it == true){
                    binding.playsong.performClick()
                }
            }

            initFlag.observe(viewLifecycleOwner){
                if (it == true){
                    binding.playsong.performClick()
                }
            }

            likeFlag.observe(viewLifecycleOwner){
                if (it == true){
                    binding.favoriteFlag.setBackgroundResource(R.drawable.favorite_24px_yes)
                }
            }

            nowMusicRecordImageList.observe(viewLifecycleOwner){
                adapter.setRecordList(it)
            }

            playbackState.observe(viewLifecycleOwner){
                when(it.state){
                    PlaybackStateCompat.STATE_PLAYING -> {
                        binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
                    }
                    else -> {
                        binding.playsong.setBackgroundResource(R.drawable.outline_play_arrow_24)
                    }
                }
            }

        }



        return binding.root
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

    override fun onStartTrackingTouch(p0: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (mainViewModel.haveMusicFlag) {
            val pos: Int = seekBar.progress
            mainViewModel.nowProcess.value = pos / 1000
            mainViewModel.lastProcess.value = -1
            mController?.transportControls?.seekTo(pos.toLong())
            mController?.transportControls?.play()
//            binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
        }
    }

    @SuppressLint("SwitchIntDef", "NonConstantResourceId")
    override fun onClick(view: View) {
        if (mainViewModel.haveMusicFlag) {
            when (view.id) {
                R.id.playsong -> {
                    when (mController?.playbackState?.state) {
                        PlaybackStateCompat.STATE_PLAYING -> {
                            mController!!.transportControls.pause()
//                            binding.playsong.setBackgroundResource(R.drawable.outline_play_arrow_24)
                        }
                        PlaybackStateCompat.STATE_PAUSED -> {
                            mController!!.transportControls.play()
//                            binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
                        }
                        PlaybackStateCompat.STATE_NONE -> {
                            Log.d(TAG, "onClick: STATE_NONE")
                            mController!!.transportControls.play()
                            if (mainViewModel.initFlag.value == true){
                                mainViewModel.initFlag.value = false
                                (requireActivity() as MainActivity).thread?.start()
                            }
//                            binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
                        }
                        PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ->{
                            mController!!.transportControls.play()
                            if (mainViewModel.changeFlag.value == true){
                                mainViewModel.changeFlag.value = false
                            }
//                            binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
                        }
                    }

                }
                R.id.nextsong -> {
                    mController?.transportControls?.skipToNext()
//                    binding.playsong.setBackgroundResource(R.drawable.outline_play_arrow_24)
                }
                R.id.lastsong -> {
                    mController?.transportControls?.skipToPrevious()
//                    binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
                }
                R.id.favorite_flag->{
                    if (!mainViewModel.allPlayListSongsCrossRef.value!!.contains(PlaylistSongCrossRef(
                                    mainViewModel.nowId.value!![1],mainViewModel.nowId.value!![0]))){
                        binding.favoriteFlag.setBackgroundResource(R.drawable.favorite_24px_yes)
                        DataBaseUtils.insertMusicSongRef(PlaylistSongCrossRef(mainViewModel.nowId.value!![1], mainViewModel.nowId.value!![0]))
                    }
                }
                R.id.repeat_mode->{
                    val bundle = Bundle()

                    when (repeatMode){
                        MediaPlaybackService.DEFAULT -> {
                            repeatMode = MediaPlaybackService.SHUFFLE
                            binding.repeatMode.setBackgroundResource(R.drawable.shuffle_24px)
                            Toast.makeText(requireContext(),"随机播放",Toast.LENGTH_LONG).show()
                        }
                        MediaPlaybackService.SHUFFLE -> {
                            repeatMode = MediaPlaybackService.REPEAT_ONE
                            binding.repeatMode.setBackgroundResource(R.drawable.repeat_one_24px)
                            Toast.makeText(requireContext(),"单曲循环",Toast.LENGTH_LONG).show()
                        }
                        MediaPlaybackService.REPEAT_ONE -> {
                            repeatMode = MediaPlaybackService.DEFAULT
                            binding.repeatMode.setBackgroundResource(R.drawable.repeat_24px)
                            Toast.makeText(requireContext(),"列表循环",Toast.LENGTH_LONG).show()
                        }
                    }
                    bundle.putInt("order",repeatMode)
                    mController?.transportControls?.sendCustomAction("0",bundle)
                }
            }
        } else {
          Toast.makeText(requireContext(),"你还没有播放列表哦，去添加歌曲吧！",Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setTitle("Play")
    }
}

