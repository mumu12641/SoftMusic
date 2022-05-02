package com.example.softmusic.playMusic

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.softmusic.MainActivity
import com.example.softmusic.MainViewModel
import com.example.softmusic.R
import com.example.softmusic.databinding.FragmentMusicPlayBinding
import java.text.SimpleDateFormat
import java.util.*

class MusicPlayFragment : Fragment(), SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private lateinit var musicPlayViewModel: MusicPlayViewModel
    private lateinit var mainViewModel : MainViewModel
    private lateinit var mController: MediaControllerCompat
    private val TAG = "MediaPlayer"
    private lateinit var _binding: FragmentMusicPlayBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicPlayViewModel = ViewModelProvider(requireActivity())[MusicPlayViewModel::class.java]
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

        mController = (requireActivity() as MainActivity).mController
        mainViewModel = (requireActivity() as MainActivity).mainViewModel

        val dateFormat = SimpleDateFormat("mm:ss", Locale.CHINA)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+00:00")

        mainViewModel.duration.observe(viewLifecycleOwner){
            binding.seekBar.max = it
            binding.durationTime.text = dateFormat.format(Date(it.toLong()))
        }

        mainViewModel.nowProcess.observe(viewLifecycleOwner){
            binding.seekBar.progress = (it *1000)
            Log.d(TAG, "onCreateView: $it")
            binding.nowTime.text = dateFormat.format(Date((it * 1000).toLong()))
        }

        mainViewModel.changeFlag.observe(viewLifecycleOwner){
            if (it == true){
                mainViewModel.changeFlag.value = false
                binding.playsong.performClick()
            }
        }

        return binding.root
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

    override fun onStartTrackingTouch(p0: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        val pos: Int = seekBar.progress
        mainViewModel.nowProcess.value = pos / 1000
        mainViewModel.lastProcess.value = -1
        mController.transportControls.seekTo(pos.toLong())
        mController.transportControls.play()
        binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
    }

    @SuppressLint("SwitchIntDef", "NonConstantResourceId")
    override fun onClick(view: View) {
        when(view.id){
            R.id.playsong->{
                when (mController.playbackState.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        mController.transportControls.pause()
                        binding.playsong.setBackgroundResource(R.drawable.outline_play_arrow_24)
                        mainViewModel.lastProcess.value = mainViewModel.nowProcess.value
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        mController.transportControls.play()
                        mainViewModel.lastProcess.value = -1
                        binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
                    }
                    PlaybackStateCompat.STATE_NONE -> {
                        Log.d(TAG, "onClick: STATE_NONE")
                        mController.transportControls.play()
                        (requireActivity() as MainActivity).thread?.start()
                        binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
                    }
                }

            }
            R.id.nextsong->{
                mController.transportControls.skipToNext()
                binding.playsong.setBackgroundResource(R.drawable.outline_play_arrow_24)

            }
            R.id.lastsong->{
                mController.transportControls.skipToPrevious()
                binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setTitle("Play")
    }
}

