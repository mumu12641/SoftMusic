package com.example.softmusic.musicSong

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.softmusic.entity.PlaylistWithSongs
import com.example.softmusic.room.DataBaseUtils
import kotlin.properties.Delegates

class MusicSongViewModel : ViewModel() {
    private lateinit var playlistWithSongsData: LiveData<PlaylistWithSongs>

    var allMediaUri:List<String> = DataBaseUtils.getAllMediaUri()

    var musicSongListId by Delegates.notNull<Long>()

    fun getPlaylistWithSongs(key:Long) : LiveData<PlaylistWithSongs>{
        playlistWithSongsData = DataBaseUtils.getLiveDataPlaylistsWithSongsById(key)
        return playlistWithSongsData
    }
    fun setSongListId(id:Long):Long{
        musicSongListId = id
        return musicSongListId
    }
    fun getMediaUriList():List<String>{
        allMediaUri = DataBaseUtils.getAllMediaUri()
        return allMediaUri
    }
}