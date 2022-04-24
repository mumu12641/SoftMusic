package com.example.softmusic.songList

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.softmusic.room.MusicDao
import com.example.softmusic.room.MusicDataBase

class MusicSongListViewModel : ViewModel() {
    private val musicSongListData = MutableLiveData<List<MusicSongList>>()
    private val musicDao: MusicDao
    val musicSongListLiveData: LiveData<List<MusicSongList>>
    fun insertMusicSongList(vararg musicSongLists: MusicSongList) {
        Thread { musicDao.insertMusicSongList(*musicSongLists) }.start()
    }

    fun deleteMusicSongList(vararg musicSongLists: MusicSongList) {
        Thread { musicDao.deleteMusicSongList(*musicSongLists) }.start()
    }

    fun updateMusicSongList(vararg musicSongLists: MusicSongList) {
        Thread { musicDao.updateMusicSongList(*musicSongLists) }.start()
    }

    fun getMusicSongListData(): MutableLiveData<List<MusicSongList>> {
        loadData()
        return musicSongListData
    }

    private fun loadData() {
        val list: MutableList<MusicSongList> = ArrayList()
        list.add(MusicSongList("我喜欢", "4/17/22", 50, "me", "none", "none"))
        list.add(MusicSongList("Jay", "4/17/22", 60, "me", "none", "none"))
        list.add(MusicSongList("沈以诚", "4/18/22", 10, "me", "none", "none"))
        musicSongListData.value = list
    }

    companion object {
        const val title = "SoftMusic"
    }

    init {
        val musicDataBase: MusicDataBase = MusicDataBase.getInstance()
        musicDao = musicDataBase.musicDao
        musicSongListLiveData = musicDao.allMusicSongList
    }
}