package com.example.softmusic.bottomSheet

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.softmusic.MainActivity
import com.example.softmusic.MainViewModel
import com.example.softmusic.databinding.BottomStarSheetBinding
import com.example.softmusic.room.DataBaseUtils
import com.example.softmusic.songList.MusicSongListViewModel
import com.example.softmusic.songList.StarListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StarBottomSheet : BottomSheetDialogFragment(){

    private lateinit var _starBottomSheet : BottomStarSheetBinding
    private val binding get() = _starBottomSheet
    private val mainViewModel: MainViewModel by lazy {
        (requireActivity() as MainActivity).mainViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _starBottomSheet = BottomStarSheetBinding.inflate(inflater,container,false)
        binding.starList.apply {
            layoutManager = GridLayoutManager(requireContext(),1,
                GridLayoutManager.VERTICAL,false)
            adapter =
                mainViewModel.currentMusicId.value?.let {
                    StarListAdapter(requireContext(),DataBaseUtils.getAllList(),it)
                }
        }
        return binding.root
    }
    companion object{
        const val TAG = "StarBottomSheet"
    }
}