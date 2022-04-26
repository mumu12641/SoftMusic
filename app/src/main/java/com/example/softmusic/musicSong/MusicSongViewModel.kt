package com.example.softmusic.musicSong

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.softmusic.room.*

class MusicSongViewModel(key: String) : ViewModel() {
    private val playlistWithSongsData: LiveData<PlaylistWithSongs> = DataBaseUtils.getLiveDataPlaylistsWithSongsByKey(key)
    private var playlistSongCrossRefData:LiveData<List<PlaylistSongCrossRef>> = DataBaseUtils.getPlayListSongCrossRefByKeys(key)
    fun getPlaylistWithSongsData() : LiveData<PlaylistWithSongs>{
        return playlistWithSongsData
    }
    fun getPlaylistSongCrossRefData() : List<PlaylistSongCrossRef>? {
        return playlistSongCrossRefData.value
    }
    val songListTitle:String = key
}