package com.example.softmusic.musicSong

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.softmusic.entity.PlaylistWithSongs
import com.example.softmusic.room.*

class MusicSongViewModel(key: Long) : ViewModel() {
    private val playlistWithSongsData: LiveData<PlaylistWithSongs> = DataBaseUtils.getLiveDataPlaylistsWithSongsById(key)
    fun getPlaylistWithSongsData() : LiveData<PlaylistWithSongs>{
        return playlistWithSongsData
    }
    val musicSongListId:Long = key
}