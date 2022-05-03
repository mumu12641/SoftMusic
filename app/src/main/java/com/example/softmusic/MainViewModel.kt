package com.example.softmusic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel(){

    var nowProcess:MutableLiveData<Int> = MutableLiveData(0)

    var duration : MutableLiveData<Int> = MutableLiveData()

    var lastProcess : MutableLiveData<Int> = MutableLiveData(-1)

    var changeFlag:MutableLiveData<Boolean> = MutableLiveData(false)

    var initFlag:MutableLiveData<Boolean> = MutableLiveData(false)

    var nowId:MutableLiveData<List<Long>> = MutableLiveData()

    var nowTitle:MutableLiveData<String> = MutableLiveData()

    var haveMusicFlag = false


}