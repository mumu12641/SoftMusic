package com.example.softmusic.musicSong

import android.Manifest
import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.softmusic.MainActivity
import com.example.softmusic.MainViewModel
import com.example.softmusic.databinding.FragmentSongBinding
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.listener.ChangePlayMusicListener
import com.example.softmusic.room.DataBaseUtils
import com.permissionx.guolindev.PermissionX


class MusicSongFragment : Fragment() {
    private lateinit var _fragmentSongBinding: FragmentSongBinding
    private val fragmentSongBinding get() = _fragmentSongBinding
    private lateinit var musicSongViewModel: MusicSongViewModel
    private val mainViewModel:MainViewModel by lazy {
        (requireActivity() as MainActivity).mainViewModel
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentSongBinding =
            FragmentSongBinding.inflate(inflater, container, false)
        assert(arguments != null)
        musicSongViewModel = ViewModelProvider(requireActivity())[MusicSongViewModel::class.java]

        fragmentSongBinding.songsList.layoutManager = GridLayoutManager(
            requireContext(), 1, GridLayoutManager.VERTICAL, false
        )
        val adapter = MusicSongAdapter(requireContext(), listOf(),
                musicSongViewModel.setSongListId(requireArguments().getLong("key")),
                object : ChangePlayMusicListener {
                    override fun changePlayMusic(musicSongId: Long, musicSongListId: Long) {
                        val list = listOf(musicSongId, musicSongListId)
                        (requireActivity() as MainActivity).mainViewModel.currentId.value = list
                        mainViewModel.currentMusicId.value = musicSongId
                    }
                },0L)
        fragmentSongBinding.songsList.adapter = adapter
        musicSongViewModel.getPlaylistWithSongs(musicSongViewModel.musicSongListId).observe(viewLifecycleOwner) {
            adapter.setMusicSongs(it.songs)
//            mainViewModel.currentMusicId.value?.let { it1 -> adapter.setSelectId(it1) }
            adapter.notifyItemChanged(0)
            fragmentSongBinding.textView.text = it?.musicSongList?.songListTitle
        }
        mainViewModel.currentMusicId.observe(viewLifecycleOwner){
//            adapter.setSelectId(it)
        }
        fragmentSongBinding.addMusicSong.setOnClickListener {
            PermissionX.init(this)
                .permissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_MEDIA_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        getLocalMusic()
                    } else {
                        Toast.makeText(requireContext(), "你拒绝了以上权限", Toast.LENGTH_LONG).show()
                    }
                }
        }
        return fragmentSongBinding.root
    }

    private fun getLocalMusic() {

        val cursor: Cursor? = requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Audio.AudioColumns.IS_MUSIC
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)) > 100 * 1000) {
                    var id = 0L
                    val song = MusicSong(
                        0,
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                        getAlbumImageUri(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))).toString(),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                    )

                    if (musicSongViewModel.getMediaUriList().isEmpty() || !musicSongViewModel.getMediaUriList().contains(song.mediaFileUri)){
                        id = DataBaseUtils.insertMusicSong(song)
                        Log.d("TAG", "getLocalMusic: " + musicSongViewModel.getMediaUriList().toString())
                    }

                    if (id == 0L){
                        DataBaseUtils.insertMusicSongRef(
                            PlaylistSongCrossRef(
                                musicSongViewModel.musicSongListId,
                                DataBaseUtils.getSongIdByUri(song.mediaFileUri)
                            )
                        )
                    } else {
                        DataBaseUtils.insertMusicSongRef(
                            PlaylistSongCrossRef(
                            musicSongViewModel.musicSongListId,
                            id
                        )
                        )
                    }
                    val songList =
                        DataBaseUtils.getMusicSongListById(musicSongViewModel.musicSongListId)
                    songList.songNumber++
                    DataBaseUtils.updateMusicSongList(songList)
                }
            }

        }
        cursor?.close()
    }

    private fun getAlbumImageUri(id: Long): Uri {
        val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
        return Uri.withAppendedPath(sArtworkUri, id.toString())
    }
}