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
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.room.DataBaseUtils
import com.example.softmusic.songList.ListBottomSheet
import java.text.SimpleDateFormat
import java.util.*


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
        repeatMode = mainViewModel.currentPlayMode.value!!
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
                            when {
                                layoutManager.findFirstCompletelyVisibleItemPosition() == currentPosition + 1 -> {
                                    binding.nextsong.performClick()
                                }
                                layoutManager.findFirstCompletelyVisibleItemPosition() == currentPosition - 1 -> {
                                    binding.lastsong.performClick()
                                }
                            }
                            currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                        }
                    }
                }
            })
            when(this@MusicPlayFragment.repeatMode){
                    MediaPlaybackService.DEFAULT -> {
                        repeatMode.setBackgroundResource(R.drawable.repeat_24px)
                    }
                    MediaPlaybackService.REPEAT_ONE -> {
                        binding.repeatMode.setBackgroundResource(R.drawable.repeat_one_24px)
                        adapter.setRecordList(listOf(mainViewModel.currentMusicId.value?.let { it1 ->
                            DataBaseUtils.getMusicSongById(
                                it1
                            )
                        }) as List<MusicSong>)
                    }
                    MediaPlaybackService.SHUFFLE -> {
                        binding.repeatMode.setImageResource(R.drawable.shuffle_24px)
                    }
                }
        }



        mainViewModel.run {
            currentMusicId.observe(viewLifecycleOwner) {
                currentPosition = mainViewModel.currentPlayList.value!!.indexOf(DataBaseUtils.getMusicSongById(it))
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

            currentPlayMode.observe(viewLifecycleOwner) {
            }

            currentPlayList.observe(viewLifecycleOwner) {
                currentPosition = mainViewModel.currentPlayList.value!!.indexOf(mainViewModel.currentMusicId.value?.let { it1 ->
                    DataBaseUtils.getMusicSongById(
                        it1
                    )
                })
                binding.snapRecyclerview.scrollToPosition(currentPosition)
                adapter.setRecordList(it)
            }

            likeFlag.observe(viewLifecycleOwner) {
                if (it == true) {
                    binding.favoriteFlag.setBackgroundResource(R.drawable.favorite_24px_yes)
                }
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

    @SuppressLint("SwitchIntDef", "NonConstantResourceId", "InflateParams")
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
                            mainViewModel.currentPlayMode.value = MediaPlaybackService.SHUFFLE
                            repeatMode = MediaPlaybackService.SHUFFLE
                            binding.repeatMode.setBackgroundResource(R.drawable.shuffle_24px)
                        }
                        MediaPlaybackService.SHUFFLE -> {
                            mainViewModel.currentPlayMode.value = MediaPlaybackService.REPEAT_ONE
                            repeatMode = MediaPlaybackService.REPEAT_ONE
                            adapter.setRecordList(listOf(mainViewModel.currentMusicId.value?.let { it1 ->
                                DataBaseUtils.getMusicSongById(
                                    it1
                                )
                            }) as List<MusicSong>)
                            binding.repeatMode.setBackgroundResource(R.drawable.repeat_one_24px)

                        }
                        MediaPlaybackService.REPEAT_ONE -> {
                            mainViewModel.currentPlayMode.value = MediaPlaybackService.DEFAULT
                            repeatMode = MediaPlaybackService.DEFAULT
                            binding.repeatMode.setBackgroundResource(R.drawable.repeat_24px)
                        }
                    }
                    bundle.putInt("order", repeatMode)
                    mController?.transportControls?.sendCustomAction(
                        MediaPlaybackService.CHANGE_MODE,
                        bundle
                    )
                }
                R.id.play_list -> {
                    val listBottomSheet = ListBottomSheet()
                    listBottomSheet.show((requireActivity() as MainActivity).supportFragmentManager,ListBottomSheet.TAG)
                }
            }
        } else {
            Toast.makeText(requireContext(), "你还没有播放列表哦，去添加歌曲吧！", Toast.LENGTH_LONG).show()
        }
    }
}

