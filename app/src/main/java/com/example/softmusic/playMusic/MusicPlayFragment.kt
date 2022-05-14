package com.example.softmusic.playMusic

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.compose.runtime.currentRecomposeScope
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
import kotlin.random.Random


class MusicPlayFragment : Fragment(), SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private lateinit var musicPlayViewModel: MusicPlayViewModel
    private lateinit var mainViewModel: MainViewModel
    private var mController: MediaControllerCompat? = null
    private val TAG = "MusicPlayFragment"
    private lateinit var _binding: FragmentMusicPlayBinding
    private val binding get() = _binding
    private var repeatMode = MediaPlaybackService.DEFAULT

    private var currentPosition = 0


    private val adapter: MusicRecordAdapter by lazy {
        MusicRecordAdapter(listOf(), requireContext())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicPlayViewModel = ViewModelProvider(requireActivity())[MusicPlayViewModel::class.java]
    }

    @SuppressLint("Recycle")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        val dateFormat = SimpleDateFormat("mm:ss", Locale.CHINA)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+00:00")

        mainViewModel = (requireActivity() as MainActivity).mainViewModel
        if (mainViewModel.haveMusicFlag) {
            mController = (requireActivity() as MainActivity).mController
        }
        _binding = FragmentMusicPlayBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(requireContext())
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
            playList.setOnClickListener(this@MusicPlayFragment)
            snapRecyclerview.layoutManager = layoutManager
            snapRecyclerview.adapter = adapter
            snapRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when (newState) {
                        SCROLL_STATE_IDLE -> {
                            if (layoutManager.findFirstCompletelyVisibleItemPosition() == currentPosition + 1) {
                                binding.nextsong.performClick()
                            } else if (layoutManager.findFirstCompletelyVisibleItemPosition() == currentPosition - 1) {
                                binding.lastsong.performClick()
                            }
                            currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                        }
                    }
                }
            })
        }



        mainViewModel.run {
            currentImageUri.observe(viewLifecycleOwner) {
                currentPosition = mainViewModel.nowMusicRecordImageList.value!!.indexOf(it)
                Log.d(TAG, "onCreateView: $currentPosition")
                Log.d(TAG, "onCreateView: $it")
                binding.snapRecyclerview.scrollToPosition(currentPosition)
            }
            duration.observe(viewLifecycleOwner) {
                binding.seekBar.max = it
                binding.durationTime.text = dateFormat.format(Date(it.toLong()))
            }

            currentProgress.observe(viewLifecycleOwner) {
                binding.seekBar.progress = (it * 1000)
                binding.nowTime.text = dateFormat.format(Date((it * 1000).toLong()))
            }

            currentTitle.observe(viewLifecycleOwner) {
                binding.songTitle.text = it
            }

            currentArtist.observe(viewLifecycleOwner) {
                binding.artistName.text = it
            }

            likeFlag.observe(viewLifecycleOwner) {
                if (it == true) {
                    binding.favoriteFlag.setBackgroundResource(R.drawable.favorite_24px_yes)
                }
            }

//            nowMusicRecordImageList.observe(viewLifecycleOwner) {
//                adapter.setRecordList(it)
//            }
            nowPlayList.observe(viewLifecycleOwner){
                adapter.setRecordList(it)
            }

            playbackState.observe(viewLifecycleOwner) {
                when (it.state) {
                    PlaybackStateCompat.STATE_PAUSED -> {
                        binding.playsong.setBackgroundResource(R.drawable.outline_play_arrow_64)
                    }
                    else -> {
                        binding.playsong.setBackgroundResource(R.drawable.outline_pause_64)
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
            mainViewModel.currentProgress.value = pos / 1000
            mainViewModel.lastProgress.value = -1
            mController?.transportControls?.seekTo(pos.toLong())
            mController?.transportControls?.play()
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
                        }
                        PlaybackStateCompat.STATE_PAUSED -> {
                            mController!!.transportControls.play()
                        }
                        PlaybackStateCompat.STATE_NONE -> {
                            mController!!.transportControls.play()
                        }
                        PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> {
                            mController!!.transportControls.play()
                        }
                    }

                }
                R.id.nextsong -> {
                    mController?.transportControls?.skipToNext()
                }
                R.id.lastsong -> {
                    mController?.transportControls?.skipToPrevious()
                }
                R.id.favorite_flag -> {
                    if (!mainViewModel.allPlayListSongsCrossRef.value!!.contains(
                            PlaylistSongCrossRef(
                                mainViewModel.currentId.value!![1], mainViewModel.currentId.value!![0]
                            )
                        )
                    ) {
                        binding.favoriteFlag.setBackgroundResource(R.drawable.favorite_24px_yes)
                        DataBaseUtils.insertMusicSongRef(
                            PlaylistSongCrossRef(
                                mainViewModel.currentId.value!![1],
                                mainViewModel.currentId.value!![0]
                            )
                        )
                    }
                }
                R.id.repeat_mode -> {
                    val bundle = Bundle()

                    when (repeatMode) {
                        MediaPlaybackService.DEFAULT -> {
                            repeatMode = MediaPlaybackService.SHUFFLE
                            binding.repeatMode.setBackgroundResource(R.drawable.shuffle_24px)
                            Toast.makeText(requireContext(), "随机播放", Toast.LENGTH_LONG).show()

                            bundle.putInt("seed", 1)
//                            mainViewModel.nowMusicRecordImageList.value =
//                                mainViewModel.nowMusicRecordImageList.value?.shuffled(Random(1))
                            Log.d(TAG, "onClick: before" + mainViewModel.nowPlayList.value)
                            Log.d(TAG, "onClick: position$currentPosition")
                            mainViewModel.nowPlayList.value =
                                mainViewModel.nowPlayList.value?.shuffled(Random(1))
                            Log.d(TAG, "onClick: after" + mainViewModel.nowPlayList.value)

                            mainViewModel.nowPlayList.value?.let {

                                currentPosition = it.indexOf(mainViewModel.currentMusicId.value?.let { it1 ->
                                    DataBaseUtils.getMusicSongById(it1)
                                })

                                Log.d(TAG, "onClick: position$currentPosition")

                                bundle.putInt("nowIndex",currentPosition)
                                binding.snapRecyclerview.scrollToPosition(currentPosition)
                            }

//                            mainViewModel.nowMusicRecordImageList.value?.let {
//                                bundle.putInt("nowIndex",
//                                    it.indexOf(mainViewModel.currentImageUri.value))
//                                currentPosition = it.indexOf(mainViewModel.currentImageUri.value)
//                                binding.snapRecyclerview.scrollToPosition(currentPosition)
//                            }

                        }
                        MediaPlaybackService.SHUFFLE -> {
                            repeatMode = MediaPlaybackService.REPEAT_ONE
                            binding.repeatMode.setBackgroundResource(R.drawable.repeat_one_24px)
                            Toast.makeText(requireContext(), "单曲循环", Toast.LENGTH_LONG).show()


                        }
                        MediaPlaybackService.REPEAT_ONE -> {
                            repeatMode = MediaPlaybackService.DEFAULT
                            binding.repeatMode.setBackgroundResource(R.drawable.repeat_24px)
                            Toast.makeText(requireContext(), "列表循环", Toast.LENGTH_LONG).show()

                            mainViewModel.nowPlayList.value = mainViewModel.rawPlayList.value
                            currentPosition = mainViewModel.nowPlayList.value?.indexOf(mainViewModel.currentMusicId.value?.let {
                                DataBaseUtils.getMusicSongById(it)
                            })!!
                            bundle.putInt("nowIndex",currentPosition)
                            binding.snapRecyclerview.scrollToPosition(currentPosition)

//                            mainViewModel.nowMusicRecordImageList.value = mainViewModel.rawMusicRecordImageList.value
//                            currentPosition = mainViewModel.nowMusicRecordImageList.value?.indexOf(mainViewModel.currentImageUri.value)!!
//                            bundle.putInt("nowIndex",currentPosition)
//                            binding.snapRecyclerview.scrollToPosition(currentPosition)
                        }
                    }
                    bundle.putInt("order", repeatMode)
                    mController?.transportControls?.sendCustomAction(
                        MediaPlaybackService.CHANGE_MODE,
                        bundle
                    )
                }
                R.id.play_list -> {
                }
            }
        } else {
            Toast.makeText(requireContext(), "你还没有播放列表哦，去添加歌曲吧！", Toast.LENGTH_LONG).show()
        }
    }
}

