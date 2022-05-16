package com.example.softmusic

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.softmusic.databinding.BottomSheetBinding
import com.example.softmusic.room.DataBaseUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheet (private val songId : Long):BottomSheetDialogFragment() {

    private lateinit var _bottomSheetBinding: BottomSheetBinding
    private val binding get() = _bottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        if (songId == 0L){
            return  inflater.inflate(R.layout.bottom_sheet,container,false)
        }
        _bottomSheetBinding = BottomSheetBinding.inflate(inflater,null,false)
        val song = DataBaseUtils.getMusicSongById(songId)
        Glide.with(requireContext())
            .load(song.songAlbum)
            .placeholder(R.drawable.music_note_150)
            .into(binding.songRecord)
        binding.textView2.text = song.songTitle
        binding.textView3.text = song.songSinger
        binding.textView4.text = song.mediaFileUri


        return binding.root
    }

    companion object{
        const val TAG = "BottomSheet"
    }
}