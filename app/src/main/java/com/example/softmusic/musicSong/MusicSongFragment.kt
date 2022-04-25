package com.example.softmusic.musicSong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.softmusic.MainActivity
import com.example.softmusic.databinding.FragmentSongBinding

class MusicSongFragment : Fragment() {
    private lateinit var _fragmentSongBinding: FragmentSongBinding
    private val fragmentSongBinding get() = _fragmentSongBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentSongBinding =
            FragmentSongBinding.inflate(inflater, container, false)
        assert(arguments != null)
        val musicSongViewModel: MusicSongViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireArguments().getString("key")!!)
        ).get<MusicSongViewModel>(
            MusicSongViewModel::class.java
        )
        fragmentSongBinding.songsList.layoutManager = GridLayoutManager(
            requireContext(), 1, GridLayoutManager.VERTICAL, false
        )
        musicSongViewModel.getPlaylistWithSongsData().observe(viewLifecycleOwner) {
            fragmentSongBinding.songsList.adapter = MusicSongAdapter(requireContext(), it?.songs)
            (requireActivity() as MainActivity).setTitle(
                it?.playlist?.songListTitle
            )
            fragmentSongBinding.textView.text = it?.playlist?.songListTitle
        }
        return fragmentSongBinding.root
    }

    internal class ViewModelFactory(private val key: String) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MusicSongViewModel(key) as T
        }

    }
}