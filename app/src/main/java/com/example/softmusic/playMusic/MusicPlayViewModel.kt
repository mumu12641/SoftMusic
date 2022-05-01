package com.example.softmusic.playMusic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicPlayViewModel:ViewModel() {
    val nowPos:MutableLiveData<Int> = MutableLiveData(0)
    var lastPos = -1
    val duration:MutableLiveData<Int> = MutableLiveData()
    val nextFlag:MutableLiveData<Boolean> = MutableLiveData(false)
    fun setAllDuration(duration1:Int){
        duration.value = duration1
    }
    fun setNow(now:Int){
        nowPos.value = now
    }
    fun postNow(now: Int){
        nowPos.postValue(now)
    }
    fun setFlag(flag:Boolean){
        nextFlag.value = flag
    }
    fun postFlag(flag: Boolean){
        nextFlag.postValue(flag)
    }
}