package com.example.softmusic.songList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.softmusic.entity.MusicSongList
import com.example.softmusic.room.DataBaseUtils

class MusicSongListViewModel : ViewModel() {
    val musicSongListLiveData: LiveData<List<MusicSongList>> = DataBaseUtils.getAllMusicSongList()
    companion object {
        const val title = "SoftMusic"
    }

}