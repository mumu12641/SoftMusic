package com.example.softmusic.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.softmusic.databinding.SearchFragmentBinding
import com.example.softmusic.listener.ChangePlayMusicListener
import com.example.softmusic.musicSong.MusicSongAdapter

class SearchFragment : Fragment() {

    private val viewModel:SearchViewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = SearchFragmentBinding.inflate(inflater,container,false)
        val adapter = MusicSongAdapter(requireContext(), listOf(),0L,object : ChangePlayMusicListener{
            override fun changePlayMusic(musicSongId: Long, musicSongListId: Long) {
            }
        },0L)
        binding.songsList.adapter = adapter
        return binding.root
    }

}