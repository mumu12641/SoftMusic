package com.example.softmusic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel(){
    private var musicSongsTitle:MutableLiveData<List<String>> = MutableLiveData()
    var nowProcess:MutableLiveData<Int> = MutableLiveData(0)
    var duration : MutableLiveData<Int> = MutableLiveData()
    var lastProcess : MutableLiveData<Int> = MutableLiveData(-1)

    var nowMusicSongId:Long = 0
    var nowMusicSongListId:Long = 0


    fun loadChildren(titles:List<String>){
        musicSongsTitle.value = titles
    }

}