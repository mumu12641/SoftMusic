package com.example.softmusic.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softmusic.entity.MusicSong
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import com.example.softmusic.network.LoadState
import com.example.softmusic.network.NetworkService
import com.example.softmusic.room.DataBaseUtils

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
                val detail = NetworkService.getDetailService.getSongDetailMsg(i.id)
                val song = media.data[0].url?.let {
                    detail.songs[0].al?.picUrl?.let { it1 ->
                        MusicSong(0L,detail.songs[0].name,detail.songs[0].ar[0].name, it1,
                            it,detail.songs[0].dt,i.id.toLong())
                    }
                }
                Log.d(TAG, "getSongResultMsg: " + detail.privileges[0].fee)
                if (detail.privileges[0].fee != 0){
                    song?.duration = 30000
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