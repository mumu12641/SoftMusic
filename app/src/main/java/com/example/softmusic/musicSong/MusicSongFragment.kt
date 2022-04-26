package com.example.softmusic.musicSong

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.softmusic.MainActivity
import com.example.softmusic.databinding.FragmentSongBinding
import com.example.softmusic.room.DataBaseUtils
import com.example.softmusic.room.PlaylistSongCrossRef
import com.example.softmusic.room.PlaylistWithSongs


class MusicSongFragment : Fragment() {
    private lateinit var _fragmentSongBinding: FragmentSongBinding
    private val fragmentSongBinding get() = _fragmentSongBinding
    private lateinit var musicSongViewModel: MusicSongViewModel
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentSongBinding =
            FragmentSongBinding.inflate(inflater, container, false)
        assert(arguments != null)
        musicSongViewModel= ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireArguments().getString("key")!!)
        ).get<MusicSongViewModel>(
            MusicSongViewModel::class.java
        )
        fragmentSongBinding.songsList.layoutManager = GridLayoutManager(
            requireContext(), 1, GridLayoutManager.VERTICAL, false
        )
        musicSongViewModel.getPlaylistWithSongsData().observe(viewLifecycleOwner) {
            fragmentSongBinding.songsList.adapter = MusicSongAdapter(requireContext(), it?.songs,
                requireArguments().getString("key")!!)
            (requireActivity() as MainActivity).setTitle(
                it?.playlist?.songListTitle
            )
            fragmentSongBinding.textView.text = it?.playlist?.songListTitle
        }
        fragmentSongBinding.addMusicSong.setOnClickListener{
            Log.d("TAG", "onCreateView: add")
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
            getLocalMusic()
        }
        return fragmentSongBinding.root
    }

    internal class ViewModelFactory(private val key: String) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MusicSongViewModel(key) as T
        }

    }
    fun getLocalMusic(){

        val cursor: Cursor? = requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Audio.AudioColumns.IS_MUSIC
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Log.d("TAG", "getLocalMusic: "+cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)))
                Log.d("TAG", "getLocalMusic: "+cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)))
                Log.d("TAG", "getLocalMusic: "+cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)))
                Log.d("TAG", "getLocalMusic: "+
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)))
                Log.d("TAG", "getLocalMusic: "+
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)))
                Log.d("TAG", "getLocalMusic: "+
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)))
                val song = MusicSong(
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                    "none",
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)))
//                if (!(musicSongViewModel.getPlaylistWithSongsData().value as PlaylistWithSongs).songs?.contains(song)!!) {
//                    DataBaseUtils.insertMusicSong(song)
//                }else{
//                    Log.d("TAG", "getLocalMusic: 已经添加")
//                }
////                val result = DataBaseUtils.getMusicSongByKey(song.songTitle)?:DataBaseUtils.insertMusicSong(song)
//                val playlistSongCrossRef = PlaylistSongCrossRef(
//                    musicSongViewModel.songListTitle,
//                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
//                )
//                if (!musicSongViewModel.getPlaylistSongCrossRefData()?.contains(playlistSongCrossRef)!!) {
//                    DataBaseUtils.insertMusicSongRef(
//                        playlistSongCrossRef
//                    )
//                }else{
//                    Log.d("TAG", "getLocalMusic: 已经添加")
//                }
//                if (song.songTitle != "jay.mp3"){
//                    DataBaseUtils.insertMusicSong(song)
//                    DataBaseUtils.insertMusicSongRef(
//                        PlaylistSongCrossRef(
//                    musicSongViewModel.songListTitle,
//                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))))
//                }
            }
        } else {
            Log.d("TAG", "getLocalMusic: null" )
        }

        cursor?.close()

    }
}