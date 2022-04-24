package com.example.softmusic.musicSong

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.softmusic.room.MusicDao
import com.example.softmusic.room.MusicDataBase
import com.example.softmusic.room.PlaylistSongCrossRef
import com.example.softmusic.room.PlaylistWithSongs
import com.example.softmusic.songList.MusicSongList

class MusicSongViewModel(private val key: String?) : ViewModel() {
    private val musicSongData = MutableLiveData<List<MusicSong>>()
    private val musicSongListData = MutableLiveData<MusicSongList>()
    private val playlistWithSongsData: LiveData<PlaylistWithSongs?>?
    private val musicDao: MusicDao?
    var test: LiveData<List<MusicSong>> = MutableLiveData()
    private val testList = MutableLiveData<MusicSongList>()

    //    public MutableLiveData<List<MusicSong>> getMusicSongData() {
    //        musicSongData.setValue(Objects.requireNonNull(playlistWithSongsData.getValue()).songs);
    //        return musicSongData;
    //    }
    //
    //    public MutableLiveData<MusicSongList> getMusicSongListData() {
    //        musicSongListData.setValue(Objects.requireNonNull(playlistWithSongsData.getValue()).playlist);
    //        return musicSongListData;
    //    }
    fun insertMusicSongRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?) {
        Thread { musicDao!!.insertPlaylistSongCrossRef(*playlistSongCrossRefs) }.start()
    }

    fun deleteMusicSongRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?) {
        Thread { musicDao!!.deletePlaylistSongCrossRef(*playlistSongCrossRefs) }.start()
    }

    fun updateMusicSongRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?) {
        Thread { musicDao!!.deletePlaylistSongCrossRef(*playlistSongCrossRefs) }.start()
    }

    fun insertMusicSong(vararg musicSongs: MusicSong?) {
        Thread { musicDao!!.insertMusicSong(*musicSongs) }.start()
    }

    fun updateMusicSong(vararg musicSongs: MusicSong?) {
        Thread { musicDao!!.updateMusicSong(*musicSongs) }.start()
    }

    fun deleteMusicSong(vararg musicSongs: MusicSong?) {
        Thread { musicDao!!.deleteMusicSong(*musicSongs) }.start()
    }

    fun getTestList(): MutableLiveData<MusicSongList> {
        testList.value = MusicSongList("我喜欢", "4/17/22", 50, "me", "none", "none")
        return testList
    }

    companion object {
        private const val TAG = "MusicSongViewModel"
    }

    init {
        val musicDataBase: MusicDataBase = MusicDataBase.Companion.getInstance()
        musicDao = musicDataBase.musicDao
        playlistWithSongsData = musicDao.getLiveDataPlayListWithKey(key)
        test = musicDao.allMusicSong
    }
}