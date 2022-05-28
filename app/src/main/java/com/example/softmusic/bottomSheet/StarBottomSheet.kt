package com.example.softmusic.bottomSheet

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.softmusic.MainActivity
import com.example.softmusic.MainViewModel
import com.example.softmusic.databinding.BottomStarSheetBinding
import com.example.softmusic.room.DataBaseUtils
import com.example.softmusic.songList.MusicSongListViewModel
import com.example.softmusic.songList.StarListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StarBottomSheet(val id:Long) : BottomSheetDialogFragment(){

    private lateinit var _starBottomSheet : BottomStarSheetBinding
    private val binding get() = _starBottomSheet
    private val job = Job()
    private val scope = CoroutineScope(job)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _starBottomSheet = BottomStarSheetBinding.inflate(inflater,container,false)
        binding.starList.apply {
            layoutManager = GridLayoutManager(requireContext(),1,
                GridLayoutManager.VERTICAL,false)
            scope.launch {
                adapter = StarListAdapter(requireContext(),DataBaseUtils.getAllList(),this@StarBottomSheet.id)
            }
        }
        return binding.root
    }
    companion object{
        const val TAG = "StarBottomSheet"
    }
}