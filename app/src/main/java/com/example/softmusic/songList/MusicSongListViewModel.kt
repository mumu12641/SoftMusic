package com.example.softmusic.songList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.softmusic.room.DataBaseUtils

class MusicSongListViewModel : ViewModel() {
    private val musicSongListData = MutableLiveData<List<MusicSongList>>()
    val musicSongListLiveData: LiveData<List<MusicSongList>> = DataBaseUtils.getAllMusicSongList()
    fun getMusicSongListData(): MutableLiveData<List<MusicSongList>> {
        loadData()
        return musicSongListData
    }

    private fun loadData() {
        val list: MutableList<MusicSongList> = ArrayList()
        list.add(MusicSongList("我喜欢", "4/17/22", 50, "me", "none", "none"))
        musicSongListData.value = list
    }
    companion object {
        const val title = "SoftMusic"
    }

}