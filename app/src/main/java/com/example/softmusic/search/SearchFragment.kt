package com.example.softmusic.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.softmusic.MainActivity
import com.example.softmusic.MainViewModel
import com.example.softmusic.databinding.SearchFragmentBinding
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.listener.ChangePlayMusicListener
import com.example.softmusic.musicSong.MusicSongAdapter
import com.example.softmusic.network.LoadState
import com.example.softmusic.playMusic.MediaPlaybackService
import com.example.softmusic.room.DataBaseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var _binding:SearchFragmentBinding
    private val binding get() = _binding
    private val mController: MediaControllerCompat by lazy {
        (requireActivity() as MainActivity).mController
    }
    private val mainViewModel:MainViewModel by lazy {
        (requireActivity() as MainActivity).mainViewModel
    }
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
            }
            override fun changePlayMusicByEntity(song: MusicSong) {
                Toast.makeText(requireContext(),"已添加到下一首播放",Toast.LENGTH_LONG).show()
                lifecycleScope.launch(Dispatchers.IO){
                    Log.d(TAG, "changePlayMusicByEntity: start")
                    val list = DataBaseUtils.dataBase.musicDao.getAllAlbumId()
                    if (list.isEmpty() || !list.contains(song.albumId)){
                        song.musicSongId = DataBaseUtils.insertMusicSong(song)
                    } else {
                        song.musicSongId = DataBaseUtils.getMusicIdByAlbumId(song.albumId)
                    }
                    val bundle = Bundle()
                    with(bundle){
                        putString("url",song.mediaFileUri)
                        putString("title",song.songTitle)
                        putString("singer",song.songSinger)
                        putInt("duration",song.duration)
                        putString("picture",song.songAlbum)
                        putLong("id",song.musicSongId)
                    }
                    mController.transportControls?.sendCustomAction(MediaPlaybackService.NEXT_TO_PLAY,bundle)
                    if (mainViewModel.requestNetwork.value == false) {
                        mainViewModel.requestNetwork.postValue(true)
                    }
                    Log.d(TAG, "changePlayMusicByEntity: end")
                }
            }
        },-1L,MusicSongAdapter.ADD_ACTION)
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
                    }
                }
            }
            searchSongs.observe(viewLifecycleOwner) {
                Log.d(TAG, "onCreateView: $it")
                adapter.setMusicSongs(it)
            }
        }
        binding.editSearch.setOnEditorActionListener(object:TextView.OnEditorActionListener{
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_DONE){
                            viewModel.getSongResultMsg(binding.editSearch.text.toString(),5)
                    adapter.setMusicSongs(listOf())
                    Toast.makeText(requireContext(),"开始搜索",Toast.LENGTH_LONG).show()
                    return true
                }
                return false
            }
        })

        return binding.root
    }

}