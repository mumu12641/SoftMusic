package com.example.softmusic.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softmusic.entity.MusicSong
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import network.LoadState
import network.NetworkService

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
            for(i in msg.result.songs){
                val media = NetworkService.getMediaService.getSongMediaMsg(i.id)
                Log.d(TAG, "getSongResultMsg: "+ i.id)
                val pictureUrl = NetworkService.getDetailService.getSongDetailMsg(i.id).songs[0].al?.picUrl
                val song = media.data[0].url?.let {
                    pictureUrl?.let { it1 -> MusicSong(0L, i.name,i.artists[0].name, it1, it,i.duration,123) }
                }
                song?.let {
                    list.add(it)
                }
            }
            searchSongs.value = list
            loadState.value = LoadState.Success()
        }

    }

}