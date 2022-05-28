package com.example.softmusic.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.softmusic.MainActivity
import com.example.softmusic.R
import com.example.softmusic.databinding.BottomSheetBinding
import com.example.softmusic.room.DataBaseUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SongBottomSheet (private val songId : Long):BottomSheetDialogFragment() {

    private lateinit var _bottomSheetBinding: BottomSheetBinding
    private val binding get() = _bottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (songId == 0L){
            return  inflater.inflate(R.layout.bottom_sheet,container,false)
        }
        _bottomSheetBinding = BottomSheetBinding.inflate(inflater,null,false)
        val song = DataBaseUtils.getMusicSongById(songId)
        Glide.with(requireContext())
            .load(song.songAlbum)
            .placeholder(R.drawable.music_note_150)
            .into(binding.songRecord)
        with(binding){
            textView2.text = song.songTitle
            textView3.text = song.songSinger
            textView4.text = song.mediaFileUri
            star.setOnClickListener{
                val starSheet = (requireActivity() as MainActivity).mainViewModel.currentMusicId.value?.let { it1 -> StarBottomSheet(it1) }
                starSheet?.show((requireActivity() as MainActivity).supportFragmentManager, StarBottomSheet.TAG)
            }
        }
        return binding.root
    }
    companion object {
        const val TAG = "SongBottomSheet"
    }
}