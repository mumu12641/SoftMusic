package com.example.softmusic

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.softmusic.room.DataBaseUtils

class MainViewModel:ViewModel(){

    val allPlayListSongsCrossRef = DataBaseUtils.getAllPlaylistSongCrossRef()

    var nowProcess:MutableLiveData<Int> = MutableLiveData(0)

    var duration : MutableLiveData<Int> = MutableLiveData()

    var lastProcess : MutableLiveData<Int> = MutableLiveData(-1)

    var changeFlag:MutableLiveData<Boolean> = MutableLiveData(false)

    var autoChangeFlag = false

    var initFlag:MutableLiveData<Boolean> = MutableLiveData(false)

    var nowId:MutableLiveData<List<Long>> = MutableLiveData()

    var nowTitle:MutableLiveData<String> = MutableLiveData()

    var haveMusicFlag = false

    var nowImageUri:MutableLiveData<String> = MutableLiveData()

    var likeFlag:MutableLiveData<Boolean> = MutableLiveData(false)
}