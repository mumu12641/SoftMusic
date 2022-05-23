package com.example.softmusic.search

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.example.softmusic.databinding.FragmentSongBinding
import com.example.softmusic.databinding.SearchFragmentBinding
import com.example.softmusic.listener.ChangePlayMusicListener
import com.example.softmusic.musicSong.MusicSongAdapter
import network.LoadState
import kotlin.math.log

class SearchFragment : Fragment() {

    private lateinit var _binding:SearchFragmentBinding
    private val binding get() = _binding

    private val viewModel:SearchViewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }
    private val TAG = "SearchFragment"

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = SearchFragmentBinding.inflate(inflater,container,false)
        val adapter = MusicSongAdapter(requireContext(), listOf(),0L,object : ChangePlayMusicListener{
            override fun changePlayMusic(musicSongId: Long, musicSongListId: Long) {
                Toast.makeText(requireContext(),"点击成功，暂时还没有做内容",Toast.LENGTH_LONG).show()
            }
        },0L)
        binding.songsList.layoutManager  = GridLayoutManager(
            requireContext(), 1, GridLayoutManager.VERTICAL, false
        )
        binding.songsList.adapter = adapter
        with(viewModel){
            loadState.observe(viewLifecycleOwner){
                when(it){
                    is LoadState.Success -> {
                        binding.loading.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(),"success",Toast.LENGTH_LONG).show()
                    }
                    is LoadState.Fail -> {
                        binding.loading.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(),"fail",Toast.LENGTH_LONG).show()
                    }
                    is LoadState.Loading -> {
                        binding.loading.visibility = View.VISIBLE
                        binding.loading.playAnimation()
//                        binding.loading.repeatMode = LottieDrawable.RESTART
                    }
                }
            }
            searchSongs.observe(viewLifecycleOwner) {
                Log.d(TAG, "onCreateView: $it")
                adapter.setMusicSongs(it)
            }
        }
        viewModel.getSongResultMsg("海阔天空",5)

        return binding.root
    }

}