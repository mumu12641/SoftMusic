package com.example.softmusic.musicSong

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.softmusic.entity.PlaylistWithSongs
import com.example.softmusic.room.*
import kotlin.properties.Delegates

class MusicSongViewModel : ViewModel() {
    private lateinit var playlistWithSongsData: LiveData<PlaylistWithSongs>
    var musicSongListId by Delegates.notNull<Long>()
    fun getPlaylistWithSongs(key:Long) : LiveData<PlaylistWithSongs>{
        playlistWithSongsData = DataBaseUtils.getLiveDataPlaylistsWithSongsById(key)
        return playlistWithSongsData
    }
    fun setSongListId(id:Long):Long{
        musicSongListId = id
        return musicSongListId
    }
}