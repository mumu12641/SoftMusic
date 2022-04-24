package com.example.softmusic.musicSong

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.softmusic.MainActivity
import com.example.softmusic.R
import com.example.softmusic.databinding.FragmentSongBinding
import com.example.softmusic.songList.MusicSongList

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
            ViewModelFactory(requireArguments().getString("key"))
        ).get<MusicSongViewModel>(
            MusicSongViewModel::class.java
        )
        //fragmentSongBinding.theMusicSongList = musicSongViewModel
        fragmentSongBinding.songsList.layoutManager = GridLayoutManager(
            requireContext(), 1, GridLayoutManager.VERTICAL, false
        )
        //        musicSongViewModel.insertMusicSongRef(new PlaylistSongCrossRef("我喜欢","aaa"));
//        musicSongViewModel.insertMusicSong(new MusicSong("aaa","周杰伦","八度空间","none"));
        musicSongViewModel.test?.observe(
            viewLifecycleOwner
        ) { musicSongs: List<MusicSong?>? ->
            fragmentSongBinding.songsList.adapter = MusicSongAdapter(requireContext(), musicSongs)
        }
        musicSongViewModel.getTestList().observe(viewLifecycleOwner,
            Observer { musicSongList: MusicSongList ->
                (requireActivity() as MainActivity).setTitle(
                    musicSongList.songListTitle
                )
            })
        return fragmentSongBinding.root
    }

    internal class ViewModelFactory(private val key: String?) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MusicSongViewModel(key) as T
        }

    }
}