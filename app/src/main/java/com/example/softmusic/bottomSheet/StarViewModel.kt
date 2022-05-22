package com.example.softmusic.bottomSheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.softmusic.entity.MusicSongList
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.room.DataBaseUtils

class StarViewModel : ViewModel(){
    val musicSongListLiveData: LiveData<List<MusicSongList>> = DataBaseUtils.getAllMusicSongList()

    val musicRef: LiveData<List<PlaylistSongCrossRef>> = DataBaseUtils.getAllPlaylistSongCrossRef()
}