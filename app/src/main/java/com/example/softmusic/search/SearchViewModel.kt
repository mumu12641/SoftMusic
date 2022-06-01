package com.example.softmusic.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.network.LoadState
import com.example.softmusic.network.NetworkService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {


    val loadState = MutableLiveData<LoadState>()

    val searchSongs = MutableLiveData<List<MusicSong>>()

    private val TAG = "SearchViewModel"

    fun getSongResultMsg(keywords:String,limit:Int){
        viewModelScope.launch (CoroutineExceptionHandler { _,e ->
            loadState.value = LoadState.Fail(e.message ?: "加载失败")
            Log.d(TAG, "getSongResultMsg: " + e.message)
        }){
            loadState.value = LoadState.Loading()
            val msg = NetworkService.getMsgService.getSongResultMsg(keywords,limit)
            val list = mutableListOf<MusicSong>()
                msg.result.songs.map {
                    val song = MusicSong(0L,it.name,it.artists[0].name,NOT_LOAD,
                            NOT_LOAD,0,it.id.toLong())
                    list.add(song)
                }
                searchSongs.value = list
                for(i in msg.result.songs) {
                    val media = NetworkService.getMediaService.getSongMediaMsg(i.id)
                    val detail = NetworkService.getDetailService.getSongDetailMsg(i.id)
                    media.data[0].url?.let {
                        list[msg.result.songs.indexOf(i)].apply {
                            mediaFileUri = it
                            duration = detail.songs[0].dt
                        }
                    }
                    detail.songs[0].al?.picUrl?.let {
                        list[msg.result.songs.indexOf(i)].songAlbum = it
                    }
                    searchSongs.value = list
                }
            }
            loadState.value = LoadState.Success()
        }

    companion object{
        const val NOT_LOAD = "null"
    }

}