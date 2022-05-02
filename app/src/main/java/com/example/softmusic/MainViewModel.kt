package com.example.softmusic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel(){

    private var musicSongsTitle:MutableLiveData<List<String>> = MutableLiveData()
    var nowProcess:MutableLiveData<Int> = MutableLiveData(0)
    var duration : MutableLiveData<Int> = MutableLiveData()
    var lastProcess : MutableLiveData<Int> = MutableLiveData(-1)
    var changeFlag:MutableLiveData<Boolean> = MutableLiveData(false)
    var nowId:MutableLiveData<List<Long>> = MutableLiveData(listOf(1,1))

    fun loadChildren(titles:List<String>){
        musicSongsTitle.value = titles
    }
}