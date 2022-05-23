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
                Log.d(TAG, "getSongResultMsg: " + media.data[0].url)
                Log.d(TAG, "https://netease-cloud-music-api-self-ten.vercel.app/song/url?id=" + i.id)
                if (media.data[0].url == null){
                    continue
                }
                val song = MusicSong(0L, i.name,i.artists[0].name,"adsf",media.data[0].url,i.duration,123
                )
                list.add(song)
            }
            searchSongs.value = list
            loadState.value = LoadState.Success()
        }

    }

}