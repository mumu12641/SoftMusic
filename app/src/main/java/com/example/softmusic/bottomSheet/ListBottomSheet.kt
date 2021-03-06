package com.example.softmusic.bottomSheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.softmusic.MainActivity
import com.example.softmusic.MainViewModel
import com.example.softmusic.databinding.BottomListSheetBinding
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.listener.ChangePlayMusicListener
import com.example.softmusic.musicSong.MusicSongAdapter
import com.example.softmusic.playMusic.MediaPlaybackService
import com.example.softmusic.room.DataBaseUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ListBottomSheet(private val mode:Int,private val num:Int) : BottomSheetDialogFragment(){

    private lateinit var _bottomListSheetBinding : BottomListSheetBinding
    private val binding get() = _bottomListSheetBinding

    private val mainViewModel: MainViewModel by lazy {
        (requireActivity() as MainActivity).mainViewModel
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bottomListSheetBinding = BottomListSheetBinding.inflate(inflater,container,false)


        val adapter = MusicSongAdapter(requireContext(),mainViewModel.currentPlayList.value,
            mainViewModel.currentId.value!![1],object : ChangePlayMusicListener {
                override fun changePlayMusic(musicSongId: Long, musicSongListId: Long) {
                    val list = listOf(musicSongId, musicSongListId)
                    (requireActivity() as MainActivity).mainViewModel.currentId.value = list
                    mainViewModel.currentMusicId.value = musicSongId
                }

                override fun changePlayMusicByEntity(song: MusicSong) {
                }
            }, mainViewModel.currentMusicId.value!!
        ,MusicSongAdapter.NONE_ACTION,null)

        with(binding){
            bottomList.layoutManager = GridLayoutManager(requireContext(),1,GridLayoutManager.VERTICAL,false)
            bottomList.adapter = adapter
            val position = mainViewModel.currentPlayList.value!!.indexOf(
                    DataBaseUtils.getMusicSongById(
                            mainViewModel.currentMusicId.value!!
                    ))
            bottomList.scrollToPosition(position)

            when(mode){
                MediaPlaybackService.DEFAULT -> {
                    textMode.text = "????????????"
                }
                MediaPlaybackService.SHUFFLE -> {
                    textMode.text = "????????????"
                }
                MediaPlaybackService.REPEAT_ONE -> {
                    textMode.text = "????????????"
                }
            }
            textSongNumber.text = "($num ???)"
        }
        return binding.root
    }
    companion object{
        const val TAG = "ListBottomSheet"
    }
}