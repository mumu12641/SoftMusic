package com.example.softmusic.playMusic

import android.annotation.SuppressLint
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
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
    private var mController: MediaControllerCompat? = null
    private val TAG = "MusicPlayFragment"
    private lateinit var _binding: FragmentMusicPlayBinding
    private val binding get() = _binding

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
        binding.apply {
            seekBar.setOnSeekBarChangeListener(this@MusicPlayFragment)
            playsong.setOnClickListener(this@MusicPlayFragment)
            nextsong.setOnClickListener(this@MusicPlayFragment)
            lastsong.setOnClickListener(this@MusicPlayFragment)
            favoriteFlag.setOnClickListener(this@MusicPlayFragment)
            repeatMode.setOnClickListener(this@MusicPlayFragment)
        }
        if (mController?.playbackState?.state == PlaybackStateCompat.STATE_PLAYING){
            binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
        }else{
            binding.playsong.setBackgroundResource(R.drawable.outline_play_arrow_24)
        }

        mainViewModel.nowImageUri.observe(viewLifecycleOwner){
            Log.d(TAG, "onCreateView: $it")
            try {
                binding.imageView2.setImageBitmap(
                    ImageDecoder.decodeBitmap
                        (ImageDecoder.createSource(requireContext().contentResolver, Uri.parse(it))))
            } catch (e:Exception){
                Log.d(TAG, "onCreateView: $e")
              binding.imageView2.setImageResource(R.drawable.music_note_150)
            }
        }

        mainViewModel.duration.observe(viewLifecycleOwner){
            binding.seekBar.max = it
            binding.durationTime.text = dateFormat.format(Date(it.toLong()))
        }

        mainViewModel.nowProcess.observe(viewLifecycleOwner){
            binding.seekBar.progress = (it *1000)
            binding.nowTime.text = dateFormat.format(Date((it * 1000).toLong()))
        }

        mainViewModel.nowTitle.observe(viewLifecycleOwner){
            binding.songTitle.text = it
        }

        mainViewModel.changeFlag.observe(viewLifecycleOwner){
            if (it == true){
                binding.playsong.performClick()
            }
        }

        mainViewModel.initFlag.observe(viewLifecycleOwner){
            if (it == true){
                binding.playsong.performClick()
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
            binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
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
                            binding.playsong.setBackgroundResource(R.drawable.outline_play_arrow_24)
                            mainViewModel.lastProcess.value = mainViewModel.nowProcess.value
                        }
                        PlaybackStateCompat.STATE_PAUSED -> {
                            mController!!.transportControls.play()
                            mainViewModel.lastProcess.value = -1
                            binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
                        }
                        PlaybackStateCompat.STATE_NONE -> {
                            Log.d(TAG, "onClick: STATE_NONE")
                            mController!!.transportControls.play()
                            if (mainViewModel.initFlag.value == true){
                                mainViewModel.initFlag.value = false
                                (requireActivity() as MainActivity).thread?.start()
                            }
                            binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
                        }
                        PlaybackStateCompat.STATE_SKIPPING_TO_NEXT ->{
                            mController!!.transportControls.play()
                            if (mainViewModel.changeFlag.value == true){
                                mainViewModel.changeFlag.value = false
                            }
                            binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
                        }
                    }

                }
                R.id.nextsong -> {
                    mController?.transportControls?.skipToNext()
                    binding.playsong.setBackgroundResource(R.drawable.outline_play_arrow_24)
                }
                R.id.lastsong -> {
                    mController?.transportControls?.skipToPrevious()
                    binding.playsong.setBackgroundResource(R.drawable.outline_pause_24)
                }
            }
        } else {
          Toast.makeText(requireContext(),"你还没有播放列表哦，去添加歌曲吧！",Toast.LENGTH_LONG).show()
        }
        when(view.id){
            R.id.favorite_flag->{

            }
            R.id.repeat_mode->{

            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setTitle("Play")
    }
}

