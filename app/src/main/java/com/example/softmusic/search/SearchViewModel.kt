package com.example.softmusic.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import network.LoadState
import network.NetworkService

class SearchViewModel : ViewModel() {
    val code = MutableLiveData<Int>()
    val loadState = MutableLiveData<LoadState>()

    private val TAG = "SearchViewModel"


    fun getSongResultMsg(keywords:String){
        viewModelScope.launch (CoroutineExceptionHandler { _,e ->
            loadState.value = LoadState.Fail(e.message ?: "加载失败")
            Log.d(TAG, "getSongResultMsg: " + e.message)
        }){
            loadState.value = LoadState.Loading()
            val c = async { NetworkService.getMsgService.getSongResultMsg(keywords) }
            code.value = c.await().code
            loadState.value = LoadState.Success()
        }

    }

}